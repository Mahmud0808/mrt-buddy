package net.adhikary.mrtbuddy.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing

@Composable
fun LiftingHeader(
    title: String,
    lifted: Boolean,
    modifier: Modifier = Modifier,
) {
    val background by animateColorAsState(
        targetValue = if (lifted) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background,
        animationSpec = tween(MrtMotion.Short4),
    )
    val shadow by animateDpAsState(
        targetValue = if (lifted) 6.dp else 0.dp,
        animationSpec = tween(MrtMotion.Short4),
    )
    Surface(
        color = background,
        shadowElevation = shadow,
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f),
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = MrtSpacing.screenPadding),
        ) {
            ScreenHeader(title = title)
        }
    }
}
