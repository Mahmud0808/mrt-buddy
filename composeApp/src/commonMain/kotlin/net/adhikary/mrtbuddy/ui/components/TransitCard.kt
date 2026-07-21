package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TransitCard(
    modifier: Modifier = Modifier,
    gradient: List<Color>? = null,
    minHeight: Dp = 200.dp,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                spring(
                    dampingRatio = 0.9f,
                    stiffness = Spring.StiffnessMediumLow,
                )
            ),
        shape = shape,
        color = if (gradient != null) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .then(
                    if (gradient != null) {
                        Modifier.background(Brush.verticalGradient(gradient))
                    } else {
                        Modifier
                    }
                )
                .heightIn(min = minHeight)
        ) {
            content()
        }
    }
}
