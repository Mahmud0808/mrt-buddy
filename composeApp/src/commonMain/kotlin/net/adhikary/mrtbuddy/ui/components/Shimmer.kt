package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing

fun Modifier.shimmer(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2f * size.width.toFloat(),
        targetValue = 2f * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )
    val variant = MaterialTheme.colorScheme.surfaceContainerHighest
    val highlight = MaterialTheme.colorScheme.surfaceContainerLow
    background(
        brush = Brush.linearGradient(
            colors = listOf(variant, highlight, variant),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
        ),
    ).onGloballyPositioned { size = it.size }
}

@Composable
fun SkeletonBalanceCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .shimmer(),
    )
}

@Composable
fun SkeletonCardItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(MaterialTheme.shapes.large)
            .shimmer(),
    )
}

@Composable
fun SkeletonTransactionRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MrtSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer(),
            )
            Spacer(modifier = Modifier.height(MrtSpacing.sm))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(12.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer(),
            )
        }
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(20.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer(),
        )
    }
}
