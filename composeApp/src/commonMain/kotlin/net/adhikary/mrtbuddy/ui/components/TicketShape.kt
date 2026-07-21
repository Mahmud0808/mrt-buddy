package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class TicketShape(
    private val cornerRadius: Dp = 24.dp,
    private val notchRadius: Dp = 10.dp,
    private val notchOffsetY: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val corner = with(density) { cornerRadius.toPx() }
        val notch = with(density) { notchRadius.toPx() }
        val y = with(density) { notchOffsetY.toPx() }.coerceIn(0f, size.height)
        val rect = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    cornerRadius = CornerRadius(corner),
                )
            )
        }
        val notches = Path().apply {
            addOval(Rect(center = Offset(0f, y), radius = notch))
            addOval(Rect(center = Offset(size.width, y), radius = notch))
        }
        return Outline.Generic(Path.combine(PathOperation.Difference, rect, notches))
    }
}

@Composable
fun TicketDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.35f),
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp),
    ) {
        drawLine(
            color = color,
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f)),
        )
    }
}
