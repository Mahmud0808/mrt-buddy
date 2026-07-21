package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.isRenderEffectSupported
import net.adhikary.mrtbuddy.ui.components.liquid.LiquidTabItem
import net.adhikary.mrtbuddy.ui.components.liquid.LiquidTabs
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.utils.LocalHapticManager

@Composable
fun BrandSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: LayerBackdrop? = null,
) {
    if (backdrop != null && isRenderEffectSupported()) {
        val haptics = LocalHapticManager.current
        val currentIndexState = rememberUpdatedState(selectedIndex)
        val currentOnSelect = rememberUpdatedState(onSelect)
        LiquidTabs(
            selectedTabIndex = remember { { currentIndexState.value } },
            onTabSelected = remember {
                { index ->
                    if (index != currentIndexState.value) {
                        haptics.tick()
                        currentOnSelect.value(index)
                    }
                }
            },
            backdrop = backdrop,
            tabsCount = options.size,
            panelHeight = 48.dp,
            modifier = modifier.fillMaxWidth(),
        ) {
            options.forEachIndexed { index, label ->
                LiquidTabItem(onClick = { onSelect(index) }) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    } else {
        FallbackSelector(
            options = options,
            selectedIndex = selectedIndex,
            onSelect = onSelect,
            modifier = modifier,
        )
    }
}

@Composable
private fun FallbackSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mrtColors = MaterialTheme.mrtColors
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = modifier.fillMaxWidth(),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(4.dp)
                .height(40.dp),
        ) {
            val segmentWidth = maxWidth / options.size
            val thumbOffset by animateDpAsState(
                targetValue = segmentWidth * selectedIndex,
                animationSpec = tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedDecelerate),
            )
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(mrtColors.mrtCardGradient),
                        CircleShape,
                    ),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    val selected = index == selectedIndex
                    val textColor by animateColorAsState(
                        targetValue = if (selected) mrtColors.onCardFace else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(MrtMotion.Short4),
                    )
                    Box(
                        modifier = Modifier
                            .width(segmentWidth)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onSelect(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
