package net.adhikary.mrtbuddy.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

interface HapticManager {
    fun tick()
    fun success()
    fun error()
    fun impact()
}

object NoopHapticManager : HapticManager {
    override fun tick() {}
    override fun success() {}
    override fun error() {}
    override fun impact() {}
}

val LocalHapticManager = staticCompositionLocalOf<HapticManager> { NoopHapticManager }

@Composable
expect fun rememberHapticManager(): HapticManager
