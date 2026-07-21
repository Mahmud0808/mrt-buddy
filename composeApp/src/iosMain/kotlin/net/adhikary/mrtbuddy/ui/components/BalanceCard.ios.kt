package net.adhikary.mrtbuddy.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.enableNfc
import mrtbuddy.composeapp.generated.resources.nfcDisabled
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun NfcDisabledContent() {
    val mrtColors = MaterialTheme.mrtColors
    StateContent {
        StateIcon()
        Spacer(modifier = Modifier.height(MrtSpacing.md))
        Surface(
            shape = CircleShape,
            color = mrtColors.warningContainer,
        ) {
            Text(
                text = stringResource(Res.string.nfcDisabled),
                style = MaterialTheme.typography.labelLarge,
                color = mrtColors.onWarningContainer,
                modifier = Modifier.padding(horizontal = MrtSpacing.md, vertical = MrtSpacing.xs),
            )
        }
        Spacer(modifier = Modifier.height(MrtSpacing.sm))
        Text(
            text = stringResource(Res.string.enableNfc),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = mrtColors.onCardFaceSecondary,
        )
    }
}
