package net.adhikary.mrtbuddy.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.appName
import mrtbuddy.composeapp.generated.resources.eduStepBalance
import mrtbuddy.composeapp.generated.resources.eduStepFare
import mrtbuddy.composeapp.generated.resources.eduStepScan
import mrtbuddy.composeapp.generated.resources.howItWorks
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.components.BalanceCard
import net.adhikary.mrtbuddy.ui.components.LiftingHeader
import net.adhikary.mrtbuddy.ui.components.TransactionHistoryList
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    uiState: MainScreenState,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val hasTransactions = uiState.transaction.isNotEmpty()
    val listState = rememberLazyListState()
    val showEducation = !hasTransactions && uiState.cardState in listOf(
        CardState.WaitingForTap,
        CardState.NoNfcSupport,
        CardState.NfcDisabled,
    )
    Column(modifier = modifier.fillMaxSize()) {
        LiftingHeader(
            title = stringResource(Res.string.appName),
            lifted = listState.canScrollBackward,
        )
        val content: @Composable () -> Unit = {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = MrtSpacing.screenPadding,
                    end = MrtSpacing.screenPadding,
                    top = MrtSpacing.xs,
                    bottom = MrtSpacing.screenPadding + paddingValues.calculateBottomPadding(),
                ),
                verticalArrangement = Arrangement.spacedBy(MrtSpacing.lg),
            ) {
                item(key = "balance") {
                    BalanceCard(
                        cardState = uiState.cardState,
                        cardName = uiState.cardName,
                        cardIdm = uiState.cardIdm,
                    )
                }
                if (showEducation) {
                    item(key = "education") {
                        EducationCard()
                    }
                }
                if (hasTransactions) {
                    item(key = "transactions") {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(MrtMotion.Medium2, easing = MrtMotion.StandardDecelerate)) +
                                expandVertically(tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedDecelerate)),
                        ) {
                            TransactionHistoryList(uiState.transactionWithAmount)
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            if (getPlatform().name != "android") {
                PullToRefreshBox(
                    isRefreshing = uiState.cardState == CardState.Reading,
                    onRefresh = { RescanManager.requestRescan() },
                ) {
                    content()
                }
            } else {
                content()
            }
        }
    }
}

@Composable
private fun EducationCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MrtSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(MrtSpacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.howItWorks),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            EducationStep(number = 1, text = stringResource(Res.string.eduStepScan))
            EducationStep(number = 2, text = stringResource(Res.string.eduStepBalance))
            EducationStep(number = 3, text = stringResource(Res.string.eduStepFare))
        }
    }
}

@Composable
private fun EducationStep(number: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MrtSpacing.md),
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = translateNumber(number),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
