package net.adhikary.mrtbuddy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.adhikary.mrtbuddy.managers.RescanManager
import net.adhikary.mrtbuddy.nfc.getNFCManager
import net.adhikary.mrtbuddy.repository.SettingsRepository
import net.adhikary.mrtbuddy.settings.model.DarkThemeConfig
import net.adhikary.mrtbuddy.ui.screens.home.MainScreen
import net.adhikary.mrtbuddy.ui.screens.onboarding.OnboardingScreen
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenAction
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenEvent
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenState
import net.adhikary.mrtbuddy.ui.screens.home.MainScreenViewModel
import net.adhikary.mrtbuddy.ui.theme.MRTBuddyTheme
import net.adhikary.mrtbuddy.utils.LocalHapticManager
import net.adhikary.mrtbuddy.utils.observeAsActions
import net.adhikary.mrtbuddy.utils.rememberHapticManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val mainVm = koinViewModel<MainScreenViewModel>()
    val nfcManager = getNFCManager()

    mainVm.events.observeAsActions { event ->
        when (event) {
            is MainScreenEvent.Error -> {}
            MainScreenEvent.ShowMessage -> {}
        }
    }

    if (RescanManager.isRescanRequested.value) {
        nfcManager.startScan()
        RescanManager.resetRescanRequest()
    }

    // Collect NFC state and card read results - runs once per composition lifecycle
    LaunchedEffect(nfcManager) {
        nfcManager.cardReadResults.collect { result ->
            result?.let {
                mainVm.onAction(MainScreenAction.UpdateCardReadResult(it))
            }
        }
    }

    LaunchedEffect(nfcManager) {
        nfcManager.cardState.collect {
            mainVm.onAction(MainScreenAction.UpdateCardState(it))
        }
    }

    // Start NFC scan once
    nfcManager.startScan()

    val state: MainScreenState by mainVm.state.collectAsState()
    val settingsRepository = koinInject<SettingsRepository>()
    val onboardingDone by settingsRepository.onboardingDone.collectAsState()

    CompositionLocalProvider(LocalHapticManager provides rememberHapticManager()) {
        LocalizedApp(
            language = state.currentLanguage
        ) {
            MRTBuddyTheme(
                darkTheme = when (state.darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                    DarkThemeConfig.DARK -> true
                    DarkThemeConfig.LIGHT -> false
                },
            ) {
                if (onboardingDone) {
                    MainScreen()
                } else {
                    OnboardingScreen(onFinish = { settingsRepository.setOnboardingDone() })
                }
            }
        }
    }
}
