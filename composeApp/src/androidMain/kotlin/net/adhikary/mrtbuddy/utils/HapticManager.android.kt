package net.adhikary.mrtbuddy.utils

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

private class AndroidHapticManager(private val view: View) : HapticManager {
    override fun tick() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    override fun success() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    override fun error() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    override fun impact() {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
}

@Composable
actual fun rememberHapticManager(): HapticManager {
    val view = LocalView.current
    return remember(view) { AndroidHapticManager(view) }
}
