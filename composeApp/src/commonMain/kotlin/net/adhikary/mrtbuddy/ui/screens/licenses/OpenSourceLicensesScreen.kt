package net.adhikary.mrtbuddy.ui.screens.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.back
import mrtbuddy.composeapp.generated.resources.open_in_new
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import net.adhikary.mrtbuddy.ui.components.SkeletonTransactionRow
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private data class LicenseEntry(
    val group: String,
    val name: String,
    val version: String,
    val projectUrl: String?,
)

private data class LicenseSection(
    val license: String,
    val entries: List<LicenseEntry>,
)

private val entryRegex =
    Regex("""\*\*\d+\*\* \*\*Group:\*\* `([^`]+)` \*\*Name:\*\* `([^`]+)` \*\*Version:\*\* `([^`]+)`""")
private val urlRegex =
    Regex("""\*\*POM Project URL\*\*: \[([^]]+)]""")

private fun parseLicenses(markdown: String): List<LicenseSection> {
    val sections = mutableListOf<LicenseSection>()
    var currentLicense: String? = null
    var currentEntries = mutableListOf<LicenseEntry>()
    var pendingEntry: LicenseEntry? = null

    fun flushEntry() {
        pendingEntry?.let { currentEntries.add(it) }
        pendingEntry = null
    }

    fun flushSection() {
        flushEntry()
        val license = currentLicense
        if (license != null && currentEntries.isNotEmpty()) {
            sections.add(LicenseSection(license, currentEntries.toList()))
        }
        currentEntries = mutableListOf()
    }

    markdown.lineSequence().forEach { line ->
        when {
            line.startsWith("## ") -> {
                flushSection()
                val title = line.removePrefix("## ").trim()
                currentLicense = title.takeUnless { it == "Dependency License Report" }
            }
            else -> {
                val entryMatch = entryRegex.find(line)
                if (entryMatch != null) {
                    flushEntry()
                    val (group, name, version) = entryMatch.destructured
                    pendingEntry = LicenseEntry(group, name, version, null)
                } else {
                    val urlMatch = urlRegex.find(line)
                    if (urlMatch != null && pendingEntry?.projectUrl == null) {
                        pendingEntry = pendingEntry?.copy(projectUrl = urlMatch.groupValues[1])
                    }
                }
            }
        }
    }
    flushSection()
    return sections
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun OpenSourceLicensesScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    paddingValues: PaddingValues
) {
    var sections by remember { mutableStateOf<List<LicenseSection>?>(null) }

    LaunchedEffect(Unit) {
        sections = withContext(Dispatchers.Default) {
            parseLicenses(Res.readBytes("files/open-source-licenses.md").decodeToString())
        }
    }

    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(Res.string.openSourceLicenses)) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            windowInsets = WindowInsets.statusBars
        )

        val loaded = sections
        if (loaded == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MrtSpacing.xl),
            ) {
                repeat(10) {
                    SkeletonTransactionRow()
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = MrtSpacing.lg,
                    end = MrtSpacing.lg,
                    top = MrtSpacing.sm,
                    bottom = MrtSpacing.lg + paddingValues.calculateBottomPadding(),
                ),
            ) {
                loaded.forEach { section ->
                    item(key = "header_${section.license}") {
                        Row(
                            modifier = Modifier.padding(
                                start = MrtSpacing.xs,
                                top = MrtSpacing.lg,
                                bottom = MrtSpacing.sm,
                            ),
                        ) {
                            Text(
                                text = section.license,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(MrtSpacing.sm))
                            Text(
                                text = "${section.entries.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    itemsIndexed(
                        items = section.entries,
                        key = { _, entry -> "${section.license}_${entry.group}_${entry.name}" },
                    ) { index, entry ->
                        LicenseRow(
                            entry = entry,
                            isFirst = index == 0,
                            isLast = index == section.entries.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LicenseRow(entry: LicenseEntry, isFirst: Boolean, isLast: Boolean) {
    val uriHandler = LocalUriHandler.current
    val corner = 16.dp
    val shape = RoundedCornerShape(
        topStart = if (isFirst) corner else 0.dp,
        topEnd = if (isFirst) corner else 0.dp,
        bottomStart = if (isLast) corner else 0.dp,
        bottomEnd = if (isLast) corner else 0.dp,
    )
    Surface(
        onClick = { entry.projectUrl?.let { uriHandler.openUri(it) } },
        enabled = entry.projectUrl != null,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                )
            },
            supportingContent = {
                Text(
                    text = "${entry.group} · ${entry.version}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingContent = {
                if (entry.projectUrl != null) {
                    Icon(
                        painter = painterResource(Res.drawable.open_in_new),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        )
    }
}
