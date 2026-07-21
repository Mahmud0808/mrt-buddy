package net.adhikary.mrtbuddy.ui.screens.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balanceAmount
import mrtbuddy.composeapp.generated.resources.calculate
import mrtbuddy.composeapp.generated.resources.chooseOrgDest
import mrtbuddy.composeapp.generated.resources.fromStation
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.rescanToCheckSufficientBalance
import mrtbuddy.composeapp.generated.resources.roundTrips
import mrtbuddy.composeapp.generated.resources.selectDestination
import mrtbuddy.composeapp.generated.resources.selectOrigin
import mrtbuddy.composeapp.generated.resources.selectStations
import mrtbuddy.composeapp.generated.resources.singleTicket
import mrtbuddy.composeapp.generated.resources.swapStations
import mrtbuddy.composeapp.generated.resources.swap_vert
import mrtbuddy.composeapp.generated.resources.tapToCheckSufficientBalance
import mrtbuddy.composeapp.generated.resources.toStation
import mrtbuddy.composeapp.generated.resources.tooLow
import mrtbuddy.composeapp.generated.resources.two_way_arrows
import mrtbuddy.composeapp.generated.resources.withMRT
import mrtbuddy.composeapp.generated.resources.yourBalance
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.nfc.service.StationService
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.components.AnimatedAmountText
import net.adhikary.mrtbuddy.ui.components.StationPickerSheet
import net.adhikary.mrtbuddy.ui.components.TransitCard
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorAction
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorState
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorViewModel
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StationSelectionSection(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    val haptics = LocalHapticManager.current
    var swapRotation by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = swapRotation,
        animationSpec = tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedDecelerate),
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(MrtSpacing.sm)) {
            StationFieldRow(
                label = stringResource(Res.string.fromStation),
                value = uiState.fromStation?.let { StationService.translate(it.name) },
                placeholder = stringResource(Res.string.selectOrigin),
                onClick = { viewModel.onAction(FareCalculatorAction.ToggleFromExpanded) },
            )
            StationFieldRow(
                label = stringResource(Res.string.toStation),
                value = uiState.toStation?.let { StationService.translate(it.name) },
                placeholder = stringResource(Res.string.selectDestination),
                onClick = { viewModel.onAction(FareCalculatorAction.ToggleToExpanded) },
            )
        }
        FilledIconButton(
            onClick = {
                val from = uiState.fromStation
                val to = uiState.toStation
                if (from != null && to != null) {
                    haptics.tick()
                    swapRotation += 180f
                    viewModel.onAction(FareCalculatorAction.UpdateFromStation(to))
                    viewModel.onAction(FareCalculatorAction.UpdateToStation(from))
                }
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = MrtSpacing.xl)
                .size(44.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.swap_vert),
                contentDescription = stringResource(Res.string.swapStations),
                modifier = Modifier.rotate(animatedRotation),
            )
        }
    }
    if (uiState.fromExpanded) {
        StationPickerSheet(
            title = stringResource(Res.string.selectOrigin),
            stations = viewModel.stations,
            selected = uiState.fromStation,
            onSelect = { viewModel.onAction(FareCalculatorAction.UpdateFromStation(it)) },
            onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
        )
    }
    if (uiState.toExpanded) {
        StationPickerSheet(
            title = stringResource(Res.string.selectDestination),
            stations = viewModel.stations,
            selected = uiState.toStation,
            onSelect = { viewModel.onAction(FareCalculatorAction.UpdateToStation(it)) },
            onDismiss = { viewModel.onAction(FareCalculatorAction.DismissDropdowns) },
        )
    }
}

@Composable
private fun StationFieldRow(
    label: String,
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = MrtSpacing.lg, vertical = MrtSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(10.dp),
            ) {}
            Spacer(modifier = Modifier.width(MrtSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = value ?: placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun FareDisplayCard(uiState: FareCalculatorState, viewModel: FareCalculatorViewModel) {
    TransitCard(minHeight = 220.dp) {
        if (getPlatform().name != "android") {
            TextButton(
                onClick = { RescanManager.requestRescan() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MrtSpacing.sm),
            ) {
                Text(stringResource(Res.string.rescan))
            }
        }
        AnimatedContent(
            targetState = uiState.fromStation != null && uiState.toStation != null,
            transitionSpec = {
                (fadeIn(tween(MrtMotion.Medium2, delayMillis = 90, easing = MrtMotion.StandardDecelerate)) +
                    scaleIn(
                        initialScale = 0.94f,
                        animationSpec = tween(MrtMotion.Medium2, delayMillis = 90, easing = MrtMotion.EmphasizedDecelerate),
                    )) togetherWith fadeOut(tween(160, easing = MrtMotion.StandardAccelerate))
            },
            modifier = Modifier.fillMaxWidth(),
        ) { hasSelection ->
            if (!hasSelection) {
                FareEmptyContent()
            } else {
                FareResultContent(uiState)
            }
        }
    }
}

@Composable
private fun FareEmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(MrtSpacing.cardPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(Res.drawable.calculate),
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(MrtSpacing.md))
        Text(
            text = stringResource(Res.string.selectStations),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(MrtSpacing.sm))
        Text(
            text = stringResource(Res.string.chooseOrgDest),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FareResultContent(uiState: FareCalculatorState) {
    val mrtColors = MaterialTheme.mrtColors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
            .padding(MrtSpacing.cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.withMRT),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(MrtSpacing.xs))
        AnimatedAmountText(
            amount = uiState.discountedFare,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(modifier = Modifier.height(MrtSpacing.xs))
        Text(
            text = "${stringResource(Res.string.singleTicket)} ৳ ${translateNumber(uiState.calculatedFare)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(MrtSpacing.lg))
        when (val cardState = uiState.cardState) {
            is CardState.Balance -> {
                val balance = cardState.amount
                if (balance >= uiState.calculatedFare) {
                    val roundTrips = if (uiState.calculatedFare > 0) balance / (uiState.discountedFare * 2) else 0
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${stringResource(Res.string.balanceAmount)} ৳ ${translateNumber(balance)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = mrtColors.positive,
                        )
                        AnimatedVisibility(visible = roundTrips > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = MrtSpacing.xs),
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.two_way_arrows),
                                    contentDescription = null,
                                    tint = mrtColors.positive,
                                )
                                Spacer(modifier = Modifier.width(MrtSpacing.xs))
                                Text(
                                    text = "${translateNumber(roundTrips)} ${stringResource(Res.string.roundTrips)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = mrtColors.positive,
                                )
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Text(
                            text = "${stringResource(Res.string.yourBalance)} ৳ ${translateNumber(balance)} ${stringResource(Res.string.tooLow)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = MrtSpacing.md, vertical = 6.dp),
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = stringResource(
                        if (getPlatform().name != "android") Res.string.rescanToCheckSufficientBalance
                        else Res.string.tapToCheckSufficientBalance
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
