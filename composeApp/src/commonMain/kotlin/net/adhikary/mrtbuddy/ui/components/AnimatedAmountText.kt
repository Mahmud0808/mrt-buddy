package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.tabular

@Composable
fun AnimatedAmountText(
    amount: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    color: Color = LocalContentColor.current,
) {
    val animate = remember { mutableStateOf(false) }
    val animatedAmount by animateIntAsState(
        targetValue = amount,
        animationSpec = if (animate.value) {
            tween(durationMillis = 650, easing = MrtMotion.EmphasizedDecelerate)
        } else {
            tween(durationMillis = 0)
        },
    )
    animate.value = true
    val finalText = "৳ ${translateNumber(amount)}"
    Text(
        text = "৳ ${translateNumber(animatedAmount)}",
        style = style.tabular,
        color = color,
        modifier = modifier.semantics { contentDescription = finalText },
    )
}
