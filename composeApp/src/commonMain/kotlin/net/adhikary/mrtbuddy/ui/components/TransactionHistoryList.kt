package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import kotlinx.datetime.LocalDateTime
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceUpdate
import mrtbuddy.composeapp.generated.resources.recentJourneys
import net.adhikary.mrtbuddy.model.TransactionType
import net.adhikary.mrtbuddy.model.TransactionWithAmount
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.nfc.service.TimestampService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.ui.theme.tabular
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransactionHistoryList(
    transactions: List<TransactionWithAmount>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MrtSpacing.cardPadding),
        ) {
            Text(
                text = stringResource(Res.string.recentJourneys),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(MrtSpacing.sm))
            val validTransactions = transactions.filter { it.transaction.timestamp.year >= 2015 }
            validTransactions.forEachIndexed { index, transactionWithAmount ->
                TransactionItem(
                    type = TransactionType.fromHeader(transactionWithAmount.transaction.fixedHeader),
                    date = transactionWithAmount.transaction.timestamp,
                    fromStation = transactionWithAmount.transaction.fromStation,
                    toStation = transactionWithAmount.transaction.toStation,
                    balance = "৳ ${transactionWithAmount.transaction.balance}",
                    amount = transactionWithAmount.amount?.let { "৳ ${translateNumber(it)}" } ?: "N/A",
                    amountValue = transactionWithAmount.amount,
                )
                if (index != validTransactions.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    type: TransactionType,
    date: LocalDateTime,
    fromStation: String,
    toStation: String,
    balance: String,
    amount: String,
    amountValue: Int?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MrtSpacing.md)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MrtSpacing.xs),
        ) {
            Text(
                text = when (type) {
                    TransactionType.BalanceUpdate -> stringResource(Res.string.balanceUpdate)
                    else -> "${StationService.translate(fromStation)} → ${StationService.translate(toStation)}"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = TimestampService.formatDateTime(date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = if (amountValue != null && amountValue > 0) "+$amount" else amount,
            style = MaterialTheme.typography.titleMedium.tabular,
            color = when {
                amountValue == null -> MaterialTheme.colorScheme.onSurface
                amountValue > 0 -> MaterialTheme.mrtColors.positive
                else -> MaterialTheme.mrtColors.negative
            },
            modifier = Modifier.padding(start = MrtSpacing.sm),
        )
    }
}
