package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.inter_bold
import mrtbuddy.composeapp.generated.resources.inter_medium
import mrtbuddy.composeapp.generated.resources.inter_regular
import mrtbuddy.composeapp.generated.resources.inter_semibold
import mrtbuddy.composeapp.generated.resources.noto_sans_bengali_medium
import mrtbuddy.composeapp.generated.resources.noto_sans_bengali_regular
import mrtbuddy.composeapp.generated.resources.noto_sans_bengali_semibold
import net.adhikary.mrtbuddy.Language
import net.adhikary.mrtbuddy.LocalLocalization
import org.jetbrains.compose.resources.Font

val TextStyle.tabular: TextStyle
    get() = copy(fontFeatureSettings = "tnum")

@Composable
private fun interFamily() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

@Composable
private fun notoBengaliFamily() = FontFamily(
    Font(Res.font.noto_sans_bengali_regular, FontWeight.Normal),
    Font(Res.font.noto_sans_bengali_medium, FontWeight.Medium),
    Font(Res.font.noto_sans_bengali_semibold, FontWeight.SemiBold),
    Font(Res.font.noto_sans_bengali_semibold, FontWeight.Bold),
)

@Composable
fun mrtTypography(): Typography {
    val family = when (LocalLocalization.current) {
        Language.Bangla.isoFormat -> notoBengaliFamily()
        else -> interFamily()
    }
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = family),
        displayMedium = base.displayMedium.copy(fontFamily = family),
        displaySmall = base.displaySmall.copy(fontFamily = family),
        headlineLarge = base.headlineLarge.copy(fontFamily = family, fontWeight = FontWeight.SemiBold),
        headlineMedium = base.headlineMedium.copy(fontFamily = family, fontWeight = FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.copy(fontFamily = family, fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontFamily = family, fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontFamily = family, fontWeight = FontWeight.Medium, letterSpacing = 0.1.sp),
        titleSmall = base.titleSmall.copy(fontFamily = family, fontWeight = FontWeight.Medium),
        bodyLarge = base.bodyLarge.copy(fontFamily = family, lineHeight = 26.sp),
        bodyMedium = base.bodyMedium.copy(fontFamily = family, lineHeight = 22.sp),
        bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = family, fontWeight = FontWeight.Medium),
        labelSmall = base.labelSmall.copy(fontFamily = family, fontWeight = FontWeight.Medium),
    )
}
