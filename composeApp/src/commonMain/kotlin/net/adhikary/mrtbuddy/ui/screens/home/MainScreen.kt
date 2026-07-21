package net.adhikary.mrtbuddy.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.balance
import mrtbuddy.composeapp.generated.resources.fare
import mrtbuddy.composeapp.generated.resources.historyTab
import mrtbuddy.composeapp.generated.resources.more
import mrtbuddy.composeapp.generated.resources.openSourceLicenses
import mrtbuddy.composeapp.generated.resources.stationMap
import mrtbuddy.composeapp.generated.resources.transactions
import net.adhikary.mrtbuddy.ui.components.AppsIcon
import net.adhikary.mrtbuddy.ui.components.CalculatorIcon
import net.adhikary.mrtbuddy.ui.components.CardIcon
import net.adhikary.mrtbuddy.ui.components.HistoryIcon
import net.adhikary.mrtbuddy.ui.components.liquid.LiquidTabItem
import net.adhikary.mrtbuddy.ui.components.liquid.LiquidTabs
import net.adhikary.mrtbuddy.ui.theme.MrtSpacing
import net.adhikary.mrtbuddy.ui.navigation.detailEnter
import net.adhikary.mrtbuddy.ui.navigation.detailExit
import net.adhikary.mrtbuddy.ui.navigation.detailPopEnter
import net.adhikary.mrtbuddy.ui.navigation.detailPopExit
import net.adhikary.mrtbuddy.ui.navigation.tabEnter
import net.adhikary.mrtbuddy.ui.navigation.tabExit
import net.adhikary.mrtbuddy.ui.screens.farecalculator.FareCalculatorScreen
import net.adhikary.mrtbuddy.ui.screens.history.HistoryScreen
import net.adhikary.mrtbuddy.ui.screens.licenses.OpenSourceLicensesScreen
import net.adhikary.mrtbuddy.ui.screens.more.MoreScreen
import net.adhikary.mrtbuddy.ui.screens.stationmap.StationMapScreen
import net.adhikary.mrtbuddy.ui.screens.transactionlist.TransactionListScreen
import net.adhikary.mrtbuddy.ui.theme.MrtMotion
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

enum class Screen(val title: StringResource) {
    Home(title = Res.string.balance),
    Calculator(title = Res.string.fare),
    More(title = Res.string.more),
    History(title = Res.string.historyTab),
    TransactionList(title = Res.string.transactions),
    StationMap(title = Res.string.stationMap),
    Licenses(title = Res.string.openSourceLicenses)
}

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.state.collectAsState()
    val backdrop = rememberLayerBackdrop()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Home.name
    )
    var selectedCardIdm by remember { mutableStateOf<String?>(null) }
    val haptics = LocalHapticManager.current

    fun navigateToTab(screen: Screen) {
        navController.navigate(screen.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = currentScreen != Screen.StationMap,
                enter = slideInVertically(tween(MrtMotion.Short4, easing = MrtMotion.EmphasizedDecelerate)) { it } +
                    fadeIn(tween(MrtMotion.Short4)),
                exit = slideOutVertically(tween(MrtMotion.Short4, easing = MrtMotion.EmphasizedAccelerate)) { it } +
                    fadeOut(tween(MrtMotion.Short2)),
            ) {
                val tabScreens = listOf(Screen.Home, Screen.Calculator, Screen.History, Screen.More)
                val currentTabIndex = remember {
                    derivedStateOf {
                        when (
                            Screen.valueOf(backStackEntry?.destination?.route ?: Screen.Home.name)
                        ) {
                            Screen.Home -> 0
                            Screen.Calculator -> 1
                            Screen.History, Screen.TransactionList -> 2
                            Screen.More, Screen.StationMap, Screen.Licenses -> 3
                        }
                    }
                }
                val tabLabels = listOf(
                    stringResource(Res.string.balance),
                    stringResource(Res.string.fare),
                    stringResource(Res.string.historyTab),
                    stringResource(Res.string.more),
                )
                val tabIcons = listOf<@Composable () -> Unit>(
                    { CardIcon() },
                    { CalculatorIcon() },
                    { HistoryIcon() },
                    { AppsIcon() },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = MrtSpacing.xl, vertical = MrtSpacing.md),
                ) {
                    LiquidTabs(
                        selectedTabIndex = remember { { currentTabIndex.value } },
                        onTabSelected = { index ->
                            haptics.tick()
                            navigateToTab(tabScreens[index])
                        },
                        backdrop = backdrop,
                        tabsCount = tabScreens.size,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        tabScreens.forEachIndexed { index, screen ->
                            LiquidTabItem(onClick = { navigateToTab(screen) }) {
                                CompositionLocalProvider(
                                    LocalContentColor provides MaterialTheme.colorScheme.onSurface
                                ) {
                                    tabIcons[index]()
                                    Text(
                                        text = tabLabels[index],
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
                .background(MaterialTheme.colorScheme.background),
            enterTransition = tabEnter,
            exitTransition = tabExit,
            popEnterTransition = tabEnter,
            popExitTransition = tabExit,
        ) {
            composable(route = Screen.Home.name) {
                HomeTab(
                    uiState = uiState,
                    paddingValues = paddingValues,
                )
            }

            composable(route = Screen.Calculator.name) {
                FareCalculatorScreen(
                    cardState = uiState.cardState,
                    paddingValues = paddingValues,
                )
            }

            composable(route = Screen.More.name) {
                MoreScreen(
                    onNavigateToStationMap = {
                        navController.navigate(Screen.StationMap.name)
                    },
                    onNavigateToLicenses = {
                        navController.navigate(Screen.Licenses.name)
                    },
                    paddingValues = paddingValues,
                )
            }

            composable(route = Screen.History.name) {
                HistoryScreen(
                    onCardSelected = { cardIdm ->
                        selectedCardIdm = cardIdm
                        navController.navigate(Screen.TransactionList.name)
                    },
                    paddingValues = paddingValues,
                )
            }

            composable(
                route = Screen.TransactionList.name,
                enterTransition = detailEnter,
                exitTransition = detailExit,
                popEnterTransition = detailPopEnter,
                popExitTransition = detailPopExit,
            ) {
                selectedCardIdm?.let { cardIdm ->
                    TransactionListScreen(
                        cardIdm = cardIdm,
                        onBack = {
                            navController.navigateUp()
                        },
                        paddingValues = paddingValues
                    )
                }
            }

            composable(
                route = Screen.StationMap.name,
                enterTransition = detailEnter,
                exitTransition = detailExit,
                popEnterTransition = detailPopEnter,
                popExitTransition = detailPopExit,
            ) {
                StationMapScreen(
                    onBack = {
                        navController.navigateUp()
                    },
                )
            }

            composable(
                route = Screen.Licenses.name,
                enterTransition = detailEnter,
                exitTransition = detailExit,
                popEnterTransition = detailPopEnter,
                popExitTransition = detailPopExit,
            ) {
                OpenSourceLicensesScreen(
                    onBack = {
                        navController.navigateUp()
                    },
                    paddingValues = paddingValues
                )
            }
        }
    }
}
