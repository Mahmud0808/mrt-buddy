package net.adhikary.mrtbuddy.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

private class IosHapticManager : HapticManager {
    private val lightImpact = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
    private val mediumImpact = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    private val notification = UINotificationFeedbackGenerator()

    override fun tick() {
        lightImpact.impactOccurred()
    }

    override fun success() {
        notification.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
    }

    override fun error() {
        notification.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
    }

    override fun impact() {
        mediumImpact.impactOccurred()
    }
}

@Composable
actual fun rememberHapticManager(): HapticManager {
    return remember { IosHapticManager() }
}
