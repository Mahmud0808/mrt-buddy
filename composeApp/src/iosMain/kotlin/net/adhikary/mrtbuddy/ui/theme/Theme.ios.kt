package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.runtime.Composable

@Composable
actual fun MRTBuddyTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MrtThemeContent(
        colorScheme = if (darkTheme) darkScheme else lightScheme,
        darkTheme = darkTheme,
        content = content,
    )
}
