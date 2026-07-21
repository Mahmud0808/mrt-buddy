package net.adhikary.mrtbuddy.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

object MrtMotion {
    const val Short2 = 100
    const val Short4 = 200
    const val Medium2 = 300
    const val Medium4 = 400
    const val Long2 = 500

    val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val StandardDecelerate = CubicBezierEasing(0f, 0f, 0f, 1f)
    val StandardAccelerate = CubicBezierEasing(0.3f, 0f, 1f, 1f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
}
