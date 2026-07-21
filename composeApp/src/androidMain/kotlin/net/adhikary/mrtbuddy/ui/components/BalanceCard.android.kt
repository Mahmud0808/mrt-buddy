package net.adhikary.mrtbuddy.ui.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.enableNfc
import mrtbuddy.composeapp.generated.resources.nfcDisabled
import mrtbuddy.composeapp.generated.resources.openNfcSettings
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun NfcDisabledContent() {
    val context = LocalContext.current
    val haptics = LocalHapticManager.current
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
        Spacer(modifier = Modifier.height(MrtSpacing.lg))
        Button(
            onClick = {
                haptics.tick()
                context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1B5E20),
            ),
        ) {
            Text(
                text = stringResource(Res.string.openNfcSettings),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
