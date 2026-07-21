package net.adhikary.mrtbuddy.ui.screens.history

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.cardId
import mrtbuddy.composeapp.generated.resources.cardOptions
import mrtbuddy.composeapp.generated.resources.delete
import mrtbuddy.composeapp.generated.resources.lastScan
import mrtbuddy.composeapp.generated.resources.mrtPass
import mrtbuddy.composeapp.generated.resources.rapidPass
import mrtbuddy.composeapp.generated.resources.rename
import mrtbuddy.composeapp.generated.resources.staleScan
import mrtbuddy.composeapp.generated.resources.toggleCardIdVisibility
import mrtbuddy.composeapp.generated.resources.unnamedCard
import mrtbuddy.composeapp.generated.resources.visibility
import mrtbuddy.composeapp.generated.resources.visibility_off
import net.adhikary.mrtbuddy.data.CardEntity
import net.adhikary.mrtbuddy.translateNumber
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.theme.mrtColors
import net.adhikary.mrtbuddy.ui.theme.tabular
import net.adhikary.mrtbuddy.utils.TimeUtils
import net.adhikary.mrtbuddy.utils.isRapidPassIdm
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CardItem(
    card: CardEntity,
    balance: Int?,
    onCardSelected: () -> Unit,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val mrtColors = MaterialTheme.mrtColors
    val isRapidPass = isRapidPassIdm(card.idm)
    val gradient = if (isRapidPass) mrtColors.rapidCardGradient else mrtColors.mrtCardGradient
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(stiffness = 600f),
    )
    var menuExpanded by remember { mutableStateOf(false) }
    var isIdVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MrtSpacing.lg, vertical = MrtSpacing.sm)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onCardSelected() }
            .semantics(mergeDescendants = true) {},
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Brush.horizontalGradient(gradient)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = MrtSpacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = card.name ?: stringResource(Res.string.unnamedCard),
                        style = MaterialTheme.typography.titleMedium,
                        color = mrtColors.onCardFace,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(Res.string.cardOptions),
                                tint = mrtColors.onCardFace,
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.rename)) },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRenameClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(Res.string.delete),
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                },
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(MrtSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(MrtSpacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.balance),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = balance?.let { "৳ ${translateNumber(it)}" } ?: "—",
                            style = MaterialTheme.typography.titleLarge.tabular.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = if (isRapidPass) mrtColors.rapidPassContainer else mrtColors.mrtPassContainer,
                    ) {
                        Text(
                            text = stringResource(if (isRapidPass) Res.string.rapidPass else Res.string.mrtPass),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isRapidPass) mrtColors.onRapidPassContainer else mrtColors.onMrtPassContainer,
                            modifier = Modifier.padding(horizontal = MrtSpacing.md, vertical = MrtSpacing.xs),
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.cardId),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = if (isIdVisible) card.idm else "•".repeat(card.idm.length),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = { isIdVisible = !isIdVisible }) {
                        Crossfade(targetState = isIdVisible) { visible ->
                            Icon(
                                painter = painterResource(
                                    if (visible) Res.drawable.visibility else Res.drawable.visibility_off
                                ),
                                contentDescription = stringResource(Res.string.toggleCardIdVisibility),
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isStale = card.lastScanTime?.let { lastScan ->
                        (Clock.System.now().toEpochMilliseconds() - lastScan) / (1000 * 60 * 60) >= 72
                    } ?: false
                    Text(
                        text = if (card.lastScanTime != null) {
                            "${stringResource(Res.string.lastScan)}: ${TimeUtils.getTimeAgoString(card.lastScanTime)}"
                        } else {
                            "${stringResource(Res.string.lastScan)}: —"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (isStale) {
                        Spacer(modifier = Modifier.width(MrtSpacing.sm))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer,
                        ) {
                            Text(
                                text = stringResource(Res.string.staleScan),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = MrtSpacing.sm, vertical = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
