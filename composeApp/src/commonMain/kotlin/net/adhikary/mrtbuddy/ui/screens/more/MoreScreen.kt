package net.adhikary.mrtbuddy.ui.screens.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.flow.collectLatest
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.aboutHeader
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetails
import mrtbuddy.composeapp.generated.resources.autoSaveCardDetailsDescription
import mrtbuddy.composeapp.generated.resources.contributors
import mrtbuddy.composeapp.generated.resources.dark_mode
import mrtbuddy.composeapp.generated.resources.dark_mode_config_dark
import mrtbuddy.composeapp.generated.resources.dark_mode_config_light
import mrtbuddy.composeapp.generated.resources.dark_mode_config_system_default
import mrtbuddy.composeapp.generated.resources.dark_mode_preference
import mrtbuddy.composeapp.generated.resources.help
import mrtbuddy.composeapp.generated.resources.helpAndSupportButton
import mrtbuddy.composeapp.generated.resources.language
import mrtbuddy.composeapp.generated.resources.license
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.nonAffiliationDisclaimer
import mrtbuddy.composeapp.generated.resources.open_in_new
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.others
import mrtbuddy.composeapp.generated.resources.policy
import mrtbuddy.composeapp.generated.resources.privacyPolicy
import mrtbuddy.composeapp.generated.resources.readOnlyDisclaimer
import mrtbuddy.composeapp.generated.resources.settings
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.station_map
import net.adhikary.mrtbuddy.Language
import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig
import net.adhikary.mrtbuddy.ui.components.BrandSelector
import net.adhikary.mrtbuddy.ui.components.LiftingHeader
import net.adhikary.mrtbuddy.ui.components.liquid.LiquidToggle
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoreScreen(
    onNavigateToStationMap: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: MoreScreenViewModel = koinViewModel()
) {
    val uriHandler = LocalUriHandler.current
    val uiState by viewModel.state.collectAsState()
    val haptics = LocalHapticManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.onAction(MoreScreenAction.OnInit)
    }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is MoreScreenEvent.Error -> {}
                is MoreScreenEvent.NavigateTooStationMap -> onNavigateToStationMap()
                is MoreScreenEvent.NavigateToLicenses -> onNavigateToLicenses()
            }
        }
    }

    val glassBackdrop = rememberLayerBackdrop()
    Column(modifier = Modifier.fillMaxSize().then(modifier)) {
        LiftingHeader(
            title = stringResource(Res.string.more),
            lifted = scrollState.canScrollBackward,
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .layerBackdrop(glassBackdrop)
                    .background(MaterialTheme.colorScheme.background),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        start = MrtSpacing.screenPadding,
                        end = MrtSpacing.screenPadding,
                        bottom = MrtSpacing.screenPadding + paddingValues.calculateBottomPadding(),
                    ),
            ) {
            SectionHeader(text = stringResource(Res.string.settings))
        SectionCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MrtSpacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.autoSaveCardDetails),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(Res.string.autoSaveCardDetailsDescription),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(MrtSpacing.lg))
                LiquidToggle(
                    selected = { uiState.autoSaveEnabled },
                    onSelect = { enabled ->
                        haptics.tick()
                        viewModel.onAction(MoreScreenAction.SetAutoSave(enabled))
                    },
                    backdrop = glassBackdrop,
                )
            }
            SectionDivider()
            ListItem(
                headlineContent = { Text(stringResource(Res.string.language)) },
                leadingContent = { SectionIcon(painterResource(Res.drawable.language)) },
                supportingContent = {
                    val languages = listOf(Language.English.isoFormat, Language.Bangla.isoFormat)
                    BrandSelector(
                        options = listOf("English", "বাংলা"),
                        selectedIndex = languages.indexOf(uiState.currentLanguage).coerceAtLeast(0),
                        onSelect = { index ->
                            haptics.tick()
                            viewModel.onAction(MoreScreenAction.SetLanguage(languages[index]))
                        },
                        modifier = Modifier.padding(top = MrtSpacing.sm),
                        backdrop = glassBackdrop,
                    )
                },
                colors = sectionListItemColors(),
            )
            SectionDivider()
            ListItem(
                headlineContent = { Text(stringResource(Res.string.dark_mode_preference)) },
                leadingContent = { SectionIcon(painterResource(Res.drawable.dark_mode)) },
                supportingContent = {
                    val configs = listOf(
                        DarkThemeConfig.FOLLOW_SYSTEM,
                        DarkThemeConfig.LIGHT,
                        DarkThemeConfig.DARK,
                    )
                    BrandSelector(
                        options = listOf(
                            stringResource(Res.string.dark_mode_config_system_default),
                            stringResource(Res.string.dark_mode_config_light),
                            stringResource(Res.string.dark_mode_config_dark),
                        ),
                        selectedIndex = configs.indexOf(uiState.darkThemeConfig).coerceAtLeast(0),
                        onSelect = { index ->
                            haptics.tick()
                            viewModel.onAction(MoreScreenAction.SetDarkThemeConfig(configs[index]))
                        },
                        modifier = Modifier.padding(top = MrtSpacing.sm),
                        backdrop = glassBackdrop,
                    )
                },
                colors = sectionListItemColors(),
            )
        }

        SectionHeader(text = stringResource(Res.string.others))
        SectionCard {
            NavigationRow(
                text = stringResource(Res.string.stationMap),
                painter = painterResource(Res.drawable.station_map),
                onClick = {
                    haptics.tick()
                    viewModel.onAction(MoreScreenAction.StationMap)
                },
            )
        }

        SectionHeader(text = stringResource(Res.string.aboutHeader))
        SectionCard {
            ExternalLinkRow(
                text = stringResource(Res.string.privacyPolicy),
                painter = painterResource(Res.drawable.policy),
                onClick = { uriHandler.openUri("https://mrtbuddy.com/privacy-policy") },
            )
            SectionDivider()
            ExternalLinkRow(
                text = stringResource(Res.string.helpAndSupportButton),
                painter = painterResource(Res.drawable.help),
                onClick = { uriHandler.openUri("https://mrtbuddy.com/support") },
            )
            SectionDivider()
            ExternalLinkRow(
                text = stringResource(Res.string.contributors),
                painter = painterResource(Res.drawable.contributors),
                onClick = { uriHandler.openUri("https://mrtbuddy.com/contributors.html") },
            )
            SectionDivider()
            NavigationRow(
                text = stringResource(Res.string.openSourceLicenses),
                painter = painterResource(Res.drawable.license),
                onClick = {
                    haptics.tick()
                    viewModel.onAction(MoreScreenAction.OpenLicenses)
                },
            )
        }

        Spacer(modifier = Modifier.height(MrtSpacing.lg))
        Text(
            text = stringResource(Res.string.nonAffiliationDisclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = MrtSpacing.sm),
        )
        Text(
            text = stringResource(Res.string.readOnlyDisclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = MrtSpacing.sm),
        )
            Text(
                text = "Copyright © 2025 Aniruddha Adhikary.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = MrtSpacing.sm),
            )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = MrtSpacing.lg, top = MrtSpacing.lg, bottom = MrtSpacing.sm),
    )
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    )
}

@Composable
private fun sectionListItemColors() = ListItemDefaults.colors(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
)

@Composable
private fun SectionIcon(painter: Painter) {
    Icon(
        painter = painter,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(24.dp),
    )
}

@Composable
private fun NavigationRow(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            headlineContent = { Text(text) },
            leadingContent = { SectionIcon(painter) },
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            colors = sectionListItemColors(),
        )
    }
}

@Composable
private fun ExternalLinkRow(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            headlineContent = { Text(text) },
            leadingContent = { SectionIcon(painter) },
            trailingContent = {
                Icon(
                    painter = painterResource(Res.drawable.open_in_new),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            },
            colors = sectionListItemColors(),
        )
    }
}
