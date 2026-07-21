package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.cancel
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.delete
import mrtbuddy.composeapp.generated.resources.deleteCard
import mrtbuddy.composeapp.generated.resources.deleteCardConfirm
import mrtbuddy.composeapp.generated.resources.errorTitle
import mrtbuddy.composeapp.generated.resources.historyTab
import mrtbuddy.composeapp.generated.resources.noCardsFound
import mrtbuddy.composeapp.generated.resources.retry
import mrtbuddy.composeapp.generated.resources.scanCardPrompt
import net.adhikary.mrtbuddy.ui.components.EmptyState
import net.adhikary.mrtbuddy.ui.components.LiftingHeader
import net.adhikary.mrtbuddy.ui.components.SkeletonCardItem
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onCardSelected: (String) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: HistoryScreenViewModel = koinViewModel(),
) {
    val uiState = viewModel.state.collectAsState().value
    val haptics = LocalHapticManager.current
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        viewModel.onAction(HistoryScreenAction.OnInit)
    }

    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cardToRename by remember { mutableStateOf<Pair<String, String?>>("" to null) }
    var cardToDelete by remember { mutableStateOf("") }
    if (showRenameDialog) {
        RenameDialog(
            currentName = cardToRename.second,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                cardToRename.first.let { cardIdm ->
                    viewModel.onAction(HistoryScreenAction.RenameCard(cardIdm, newName))
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = MaterialTheme.shapes.large,
            title = { Text(stringResource(Res.string.deleteCard)) },
            text = { Text(stringResource(Res.string.deleteCardConfirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        haptics.impact()
                        viewModel.onAction(HistoryScreenAction.DeleteCard(cardToDelete))
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(stringResource(Res.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().then(modifier)) {
        LiftingHeader(
            title = stringResource(Res.string.historyTab),
            lifted = listState.canScrollBackward,
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading && uiState.cards.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MrtSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(MrtSpacing.lg),
                    ) {
                        repeat(3) {
                            SkeletonCardItem()
                        }
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            title = stringResource(Res.string.errorTitle),
                            body = uiState.error,
                            icon = painterResource(Res.drawable.card),
                            actionLabel = stringResource(Res.string.retry),
                            onAction = { viewModel.onAction(HistoryScreenAction.OnInit) },
                        )
                    }
                }
                uiState.cards.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            title = stringResource(Res.string.noCardsFound),
                            body = stringResource(Res.string.scanCardPrompt),
                            icon = painterResource(Res.drawable.card),
                        )
                    }
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isLoading,
                        onRefresh = { viewModel.onAction(HistoryScreenAction.OnInit) },
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = MrtSpacing.sm,
                                bottom = MrtSpacing.sm + paddingValues.calculateBottomPadding(),
                            ),
                        ) {
                            items(uiState.cards, key = { it.card.idm }) { cardWithBalance ->
                                Box(modifier = Modifier.fillMaxWidth().animateItem()) {
                                    CardItem(
                                        card = cardWithBalance.card,
                                        balance = cardWithBalance.balance,
                                        onCardSelected = { onCardSelected(cardWithBalance.card.idm) },
                                        onRenameClick = {
                                            cardToRename = cardWithBalance.card.idm to cardWithBalance.card.name
                                            showRenameDialog = true
                                        },
                                        onDeleteClick = {
                                            cardToDelete = cardWithBalance.card.idm
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
