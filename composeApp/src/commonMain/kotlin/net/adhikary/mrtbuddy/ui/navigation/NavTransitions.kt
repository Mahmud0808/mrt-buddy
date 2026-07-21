package net.adhikary.mrtbuddy.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import net.adhikary.mrtbuddy.ui.theme.MrtMotion

val tabEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(tween(MrtMotion.Short4, delayMillis = 90, easing = MrtMotion.StandardDecelerate)) +
        scaleIn(
            initialScale = 0.92f,
            animationSpec = tween(MrtMotion.Short4, delayMillis = 90, easing = MrtMotion.StandardDecelerate),
        )
}

val tabExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(90, easing = MrtMotion.StandardAccelerate))
}

val detailEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedDecelerate)) { it / 4 } +
        fadeIn(tween(MrtMotion.Short4))
}

val detailExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(MrtMotion.Short2, easing = MrtMotion.StandardAccelerate))
}

val detailPopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(tween(MrtMotion.Short4, delayMillis = 45, easing = MrtMotion.StandardDecelerate))
}

val detailPopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(tween(MrtMotion.Medium2, easing = MrtMotion.EmphasizedAccelerate)) { it / 4 } +
        fadeOut(tween(MrtMotion.Short2))
}
