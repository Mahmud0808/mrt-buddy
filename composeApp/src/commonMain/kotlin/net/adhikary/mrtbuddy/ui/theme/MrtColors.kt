package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MrtColors(
    val rapidPass: Color,
    val onRapidPass: Color,
    val rapidPassContainer: Color,
    val onRapidPassContainer: Color,
    val mrtPass: Color,
    val onMrtPass: Color,
    val mrtPassContainer: Color,
    val onMrtPassContainer: Color,
    val positive: Color,
    val positiveContainer: Color,
    val onPositiveContainer: Color,
    val negative: Color,
    val negativeContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val mrtCardGradient: List<Color>,
    val rapidCardGradient: List<Color>,
    val onCardFace: Color,
    val onCardFaceSecondary: Color,
)

val LightMrtColors = MrtColors(
    rapidPass = Color(0xFF0F62C0),
    onRapidPass = Color.White,
    rapidPassContainer = Color(0xFFD7E7FF),
    onRapidPassContainer = Color(0xFF06305F),
    mrtPass = Color(0xFF3D8B28),
    onMrtPass = Color.White,
    mrtPassContainer = Color(0xFFC0F0A6),
    onMrtPassContainer = Color(0xFF072100),
    positive = Color(0xFF2E7D32),
    positiveContainer = Color(0xFFD8F0D3),
    onPositiveContainer = Color(0xFF0B2E0E),
    negative = Color(0xFFBA1A1A),
    negativeContainer = Color(0xFFFFDAD6),
    warning = Color(0xFF8A5100),
    warningContainer = Color(0xFFFFDDB4),
    onWarningContainer = Color(0xFF2C1700),
    mrtCardGradient = listOf(Color(0xFF2F7A1E), Color(0xFF1B5E20)),
    rapidCardGradient = listOf(Color(0xFF1E88E5), Color(0xFF0D3C7B)),
    onCardFace = Color.White,
    onCardFaceSecondary = Color(0xCCFFFFFF),
)

val DarkMrtColors = MrtColors(
    rapidPass = Color(0xFF9ECAFF),
    onRapidPass = Color(0xFF00315F),
    rapidPassContainer = Color(0xFF1F4876),
    onRapidPassContainer = Color(0xFFD7E7FF),
    mrtPass = Color(0xFFA0D683),
    onMrtPass = Color(0xFF0F3900),
    mrtPassContainer = Color(0xFF265018),
    onMrtPassContainer = Color(0xFFC0F0A6),
    positive = Color(0xFF8BD88F),
    positiveContainer = Color(0xFF1E4622),
    onPositiveContainer = Color(0xFFD8F0D3),
    negative = Color(0xFFFFB4AB),
    negativeContainer = Color(0xFF93000A),
    warning = Color(0xFFFFB868),
    warningContainer = Color(0xFF663D00),
    onWarningContainer = Color(0xFFFFDDB4),
    mrtCardGradient = listOf(Color(0xFF2F6B23), Color(0xFF15421A)),
    rapidCardGradient = listOf(Color(0xFF1B5AA8), Color(0xFF0A2F60)),
    onCardFace = Color.White,
    onCardFaceSecondary = Color(0xB3FFFFFF),
)

val LocalMrtColors = staticCompositionLocalOf { LightMrtColors }

val MaterialTheme.mrtColors: MrtColors
    @Composable @ReadOnlyComposable get() = LocalMrtColors.current
