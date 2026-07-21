package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.errorTitle
import mrtbuddy.composeapp.generated.resources.hold
import mrtbuddy.composeapp.generated.resources.keepCardSteady
import mrtbuddy.composeapp.generated.resources.latestBalance
import mrtbuddy.composeapp.generated.resources.lowBalance
import mrtbuddy.composeapp.generated.resources.lowBalanceWarning
import mrtbuddy.composeapp.generated.resources.mrtPass
import mrtbuddy.composeapp.generated.resources.noNfcSupport
import mrtbuddy.composeapp.generated.resources.rapidPass
import mrtbuddy.composeapp.generated.resources.readingCard
import mrtbuddy.composeapp.generated.resources.requiredNfc
import mrtbuddy.composeapp.generated.resources.rescan
import mrtbuddy.composeapp.generated.resources.tap
import mrtbuddy.composeapp.generated.resources.tapRescanToStart
import net.adhikary.mrtbuddy.getPlatform
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import net.adhikary.mrtbuddy.utils.isRapidPassIdm
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val TicketHeaderHeight = 60.dp

@Composable
fun BalanceCard(
    cardState: CardState,
    cardIdm: String? = null,
    cardName: String? = null,
    modifier: Modifier = Modifier
) {
    val isRapidPass = cardIdm?.let { isRapidPassIdm(it) } ?: false
    val mrtColors = MaterialTheme.mrtColors
    val gradient = if (isRapidPass) mrtColors.rapidCardGradient else mrtColors.mrtCardGradient

    val haptics = LocalHapticManager.current
    val previousState = remember { mutableStateOf<CardState?>(null) }
    LaunchedEffect(cardState) {
        val previous = previousState.value
        if (previous is CardState.Reading && cardState is CardState.Balance) haptics.success()
        if (cardState is CardState.Error && previous !is CardState.Error) haptics.error()
        previousState.value = cardState
    }

    TransitCard(
        modifier = modifier,
        gradient = gradient,
        minHeight = 240.dp,
        shadowElevation = 8.dp,
        shape = TicketShape(cornerRadius = 28.dp, notchOffsetY = TicketHeaderHeight),
    ) {
        Icon(
            painter = painterResource(Res.drawable.card),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(170.dp)
                .offset(x = 40.dp, y = 40.dp)
                .alpha(0.07f),
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TicketHeaderHeight)
                    .padding(horizontal = MrtSpacing.cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val cardTypeKnown = cardIdm != null
                Text(
                    text = cardName.takeUnless { it.isNullOrBlank() }
                        ?: if (cardTypeKnown) {
                            stringResource(if (isRapidPass) Res.string.rapidPass else Res.string.mrtPass)
                        } else {
                            stringResource(Res.string.balance)
                        },
                    style = MaterialTheme.typography.titleMedium,
                    color = mrtColors.onCardFace,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (getPlatform().name != "android") {
                        TextButton(onClick = { RescanManager.requestRescan() }) {
                            Text(
                                text = stringResource(Res.string.rescan),
                                color = mrtColors.onCardFace,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        Spacer(modifier = Modifier.size(MrtSpacing.sm))
                    }
                    if (cardTypeKnown) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.16f),
                        ) {
                            Text(
                                text = stringResource(if (isRapidPass) Res.string.rapidPass else Res.string.mrtPass),
                                style = MaterialTheme.typography.labelMedium,
                                color = mrtColors.onCardFace,
                                modifier = Modifier.padding(horizontal = MrtSpacing.md, vertical = MrtSpacing.xs),
                            )
                        }
                    }
                }
            }
            TicketDivider(modifier = Modifier.padding(horizontal = MrtSpacing.cardPadding))
            AnimatedContent(
                targetState = cardState,
                transitionSpec = {
                    (fadeIn(tween(MrtMotion.Medium2, delayMillis = 90, easing = MrtMotion.StandardDecelerate)) +
                        scaleIn(
                            initialScale = 0.94f,
                            animationSpec = tween(MrtMotion.Medium2, delayMillis = 90, easing = MrtMotion.EmphasizedDecelerate),
                        )) togetherWith fadeOut(tween(160, easing = MrtMotion.StandardAccelerate))
                },
                modifier = Modifier.fillMaxWidth(),
            ) { state ->
                when (state) {
                    is CardState.Balance -> BalanceContent(amount = state.amount)
                    CardState.Reading -> StateContent {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 3.dp,
                            color = MaterialTheme.mrtColors.onCardFace,
                        )
                        Spacer(modifier = Modifier.height(MrtSpacing.lg))
                        Text(
                            text = stringResource(Res.string.readingCard),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.mrtColors.onCardFace,
                        )
                        Spacer(modifier = Modifier.height(MrtSpacing.sm))
                        Text(
                            text = stringResource(Res.string.keepCardSteady),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.mrtColors.onCardFaceSecondary,
                        )
                    }
                    CardState.WaitingForTap -> WaitingContent()
                    is CardState.Error -> ErrorContent(message = state.message)
                    CardState.NoNfcSupport -> StateContent {
                        StateIcon()
                        Spacer(modifier = Modifier.height(MrtSpacing.md))
                        Text(
                            text = stringResource(Res.string.noNfcSupport),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.mrtColors.onCardFace,
                        )
                        Spacer(modifier = Modifier.height(MrtSpacing.sm))
                        Text(
                            text = stringResource(Res.string.requiredNfc),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.mrtColors.onCardFaceSecondary,
                        )
                    }
                    CardState.NfcDisabled -> NfcDisabledContent()
                }
            }
        }
    }
}

@Composable
internal fun StateContent(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .padding(MrtSpacing.cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        content()
    }
}

@Composable
internal fun StateIcon() {
    Icon(
        painter = painterResource(Res.drawable.card),
        contentDescription = null,
        modifier = Modifier.size(44.dp),
        tint = MaterialTheme.mrtColors.onCardFace,
    )
}

@Composable
private fun BalanceContent(amount: Int) {
    val mrtColors = MaterialTheme.mrtColors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .padding(MrtSpacing.cardPadding),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(Res.string.latestBalance),
            style = MaterialTheme.typography.labelLarge,
            color = mrtColors.onCardFaceSecondary,
        )
        Spacer(modifier = Modifier.height(MrtSpacing.xs))
        AnimatedAmountText(
            amount = amount,
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.SemiBold),
            color = mrtColors.onCardFace,
        )
        AnimatedVisibility(
            visible = amount <= 70,
            enter = fadeIn(tween(MrtMotion.Medium2)) +
                expandVertically(tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedDecelerate)),
        ) {
            Column {
                Spacer(modifier = Modifier.height(MrtSpacing.md))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = stringResource(
                            if (amount <= 20) Res.string.lowBalance else Res.string.lowBalanceWarning
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = MrtSpacing.md, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PulsingCircle(iconSize: Dp) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val density = LocalDensity.current
    val initialRadiusPx = with(density) { iconSize.toPx() / 2 }
    val targetRadiusPx = initialRadiusPx * 2

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = initialRadiusPx,
        targetValue = targetRadiusPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val circleColor = Color.White.copy(alpha = pulseAlpha)

    Canvas(
        modifier = Modifier.size(iconSize * 2)
    ) {
        drawCircle(
            color = circleColor,
            radius = pulseRadius,
            center = center
        )
    }
}

@Composable
private fun WaitingContent() {
    StateContent {
        Box(contentAlignment = Alignment.Center) {
            PulsingCircle(iconSize = 48.dp)
            Icon(
                painter = painterResource(Res.drawable.card),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.mrtColors.onCardFace,
            )
        }
        Spacer(modifier = Modifier.height(MrtSpacing.sm))
        Text(
            text = stringResource(Res.string.tap),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.mrtColors.onCardFace,
        )
        Spacer(modifier = Modifier.height(MrtSpacing.sm))
        Text(
            text = stringResource(
                if (getPlatform().name != "android") Res.string.tapRescanToStart else Res.string.hold
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.mrtColors.onCardFaceSecondary,
        )
    }
}

@Composable
private fun ErrorContent(message: String) {
    StateContent {
        StateIcon()
        Spacer(modifier = Modifier.height(MrtSpacing.md))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer,
        ) {
            Text(
                text = stringResource(Res.string.errorTitle),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(horizontal = MrtSpacing.md, vertical = MrtSpacing.xs),
            )
        }
        Spacer(modifier = Modifier.height(MrtSpacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.mrtColors.onCardFaceSecondary,
        )
        if (getPlatform().name != "android") {
            Spacer(modifier = Modifier.height(MrtSpacing.sm))
            TextButton(onClick = { RescanManager.requestRescan() }) {
                Text(
                    text = stringResource(Res.string.rescan),
                    color = MaterialTheme.mrtColors.onCardFace,
                )
            }
        }
    }
}

@Composable
internal expect fun NfcDisabledContent()
