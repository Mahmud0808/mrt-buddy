package net.adhikary.mrtbuddy.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.apps
import mrtbuddy.composeapp.generated.resources.calculate
import mrtbuddy.composeapp.generated.resources.card
import mrtbuddy.composeapp.generated.resources.history
import org.jetbrains.compose.resources.painterResource

@Composable
fun CalculatorIcon() {
    Icon(
        painter = painterResource(Res.drawable.calculate),
        contentDescription = null
    )
}

@Composable
fun CardIcon() {
    Icon(
        painter = painterResource(Res.drawable.card),
        contentDescription = null
    )
}

@Composable
fun AppsIcon() {
    Icon(
        painter = painterResource(Res.drawable.apps),
        contentDescription = null
    )
}

@Composable
fun HistoryIcon() {
    Icon(
        painter = painterResource(Res.drawable.history),
        contentDescription = null
    )
}
