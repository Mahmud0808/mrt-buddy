package net.adhikary.mrtbuddy.ui.screens.transactionlist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.back
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.endOfTransactionHistory
import mrtbuddy.composeapp.generated.resources.errorLoadingTransactions
import mrtbuddy.composeapp.generated.resources.exportCsv
import mrtbuddy.composeapp.generated.resources.exportError
import mrtbuddy.composeapp.generated.resources.exportingTransactions
import mrtbuddy.composeapp.generated.resources.noTransactionsFound
import mrtbuddy.composeapp.generated.resources.ok
import mrtbuddy.composeapp.generated.resources.retry
import mrtbuddy.composeapp.generated.resources.transactionsAppearPrompt
import mrtbuddy.composeapp.generated.resources.unnamedCard
import net.adhikary.mrtbuddy.data.TransactionEntityWithAmount
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.components.EmptyState
import net.adhikary.mrtbuddy.ui.components.SkeletonTransactionRow
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.ui.theme.tabular
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    modifier: Modifier = Modifier,
    cardIdm: String,
    onBack: () -> Unit,
    paddingValues: PaddingValues
) {
    val viewModel: TransactionListViewModel = koinViewModel(
        key = cardIdm,
        parameters = { parametersOf(cardIdm) }
    )

    val state = viewModel.state.collectAsState().value
    val lazyListState = rememberLazyListState()
    val haptics = LocalHapticManager.current

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            val layoutInfo = lazyListState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItems - 5) && totalItems > 0
        }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    viewModel.loadMoreTransactions()
                }
            }
    }

    val wasExporting = remember { mutableStateOf(false) }
    LaunchedEffect(state.isExporting, state.exportError) {
        if (wasExporting.value && !state.isExporting) {
            if (state.exportError == null) haptics.success() else haptics.error()
        }
        wasExporting.value = state.isExporting
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.cardName?.takeIf { it.isNotBlank() }
                                ?: stringResource(Res.string.unnamedCard),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        state.balance?.let { balance ->
                            Text(
                                text = "৳ ${translateNumber(balance)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                windowInsets = WindowInsets.statusBars,
                actions = {
                    IconButton(
                        onClick = { viewModel.exportTransactions() },
                        enabled = !state.isExporting && state.transactions.isNotEmpty()
                    ) {
                        if (state.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(Res.string.exportCsv)
                            )
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    state.isLoading && state.transactions.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(MrtSpacing.xl),
                            verticalArrangement = Arrangement.spacedBy(MrtSpacing.sm),
                        ) {
                            repeat(8) {
                                SkeletonTransactionRow()
                            }
                        }
                    }

                    state.error != null && state.transactions.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(MrtSpacing.xxl),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(MrtSpacing.lg))
                            Text(
                                text = state.error ?: stringResource(Res.string.errorLoadingTransactions),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(MrtSpacing.xl))
                            Button(onClick = { viewModel.retry() }) {
                                Text(stringResource(Res.string.retry))
                            }
                        }
                    }

                    state.transactions.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = paddingValues.calculateBottomPadding()),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                title = stringResource(Res.string.noTransactionsFound),
                                body = stringResource(Res.string.transactionsAppearPrompt),
                            )
                        }
                    }

                    else -> {
                        PullToRefreshBox(
                            isRefreshing = state.isLoading && state.transactions.isNotEmpty(),
                            onRefresh = { viewModel.refresh() },
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            TransactionList(
                                state = state,
                                lazyListState = lazyListState,
                                paddingValues = paddingValues,
                                onRetry = { viewModel.retry() }
                            )
                        }
                    }
                }
            }
        }

        if (state.isExporting) {
            ExportLoadingOverlay()
        }

        state.exportError?.let { error ->
            ExportErrorDialog(
                errorMessage = error,
                onDismiss = { viewModel.clearExportError() }
            )
        }
    }
}

@Composable
private fun TransactionList(
    state: TransactionListState,
    lazyListState: LazyListState,
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = MrtSpacing.lg,
            start = MrtSpacing.xl,
            end = MrtSpacing.xl,
            bottom = MrtSpacing.xl + paddingValues.calculateBottomPadding()
        ),
    ) {
        itemsIndexed(
            items = state.transactions,
            key = { _, transaction ->
                "${transaction.transactionEntity.cardIdm}_${transaction.transactionEntity.scanId}_${transaction.transactionEntity.fromStation}_${transaction.transactionEntity.toStation}_${transaction.transactionEntity.dateTime}_${transaction.transactionEntity.order}"
            }
        ) { index, transaction ->
            Column(modifier = Modifier.animateItem()) {
                TransactionItem(transaction)
                if (index < state.transactions.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        if (state.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MrtSpacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        if (state.error != null && state.transactions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MrtSpacing.lg),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(MrtSpacing.sm))
                    TextButton(onClick = onRetry) {
                        Text(stringResource(Res.string.retry))
                    }
                }
            }
        }

        if (!state.canLoadMore && !state.isLoadingMore && state.transactions.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.endOfTransactionHistory),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MrtSpacing.lg),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TransactionItem(trxEntity: TransactionEntityWithAmount) {
    val transaction = trxEntity.transactionEntity
    val transactionType = TransactionType.fromHeader(transaction.fixedHeader)
    val amountText = if (trxEntity.amount != null) {
        if (trxEntity.amount > 0) "+৳ ${translateNumber(trxEntity.amount)}" else "৳ ${translateNumber(trxEntity.amount)}"
    } else {
        "N/A"
    }
    val tz = TimestampService.getDefaultTimezone()
    val dateTimeFormatted = TimestampService.formatDateTime(
        Instant.fromEpochMilliseconds(transaction.dateTime).toLocalDateTime(tz)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MrtSpacing.md)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MrtSpacing.xs),
        ) {
            Text(
                text = when (transactionType) {
                    TransactionType.BalanceUpdate -> stringResource(Res.string.balanceUpdate)
                    else -> "${StationService.translate(transaction.fromStation)} → ${
                        StationService.translate(transaction.toStation)
                    }"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = dateTimeFormatted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = amountText,
            style = MaterialTheme.typography.titleMedium.tabular,
            color = when {
                trxEntity.amount == null -> MaterialTheme.colorScheme.onSurface
                trxEntity.amount > 0 -> MaterialTheme.mrtColors.positive
                else -> MaterialTheme.mrtColors.negative
            },
            modifier = Modifier.padding(start = MrtSpacing.sm),
        )
    }
}

@Composable
private fun ExportLoadingOverlay() {
    val scrimAlpha by animateFloatAsState(
        targetValue = 0.5f,
        animationSpec = tween(MrtMotion.Short4),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = scrimAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(MrtSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(MrtSpacing.lg))
                Text(
                    text = stringResource(Res.string.exportingTransactions),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ExportErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        title = { Text(stringResource(Res.string.exportError)) },
        text = { Text(errorMessage) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(Res.string.ok))
            }
        }
    )
}
