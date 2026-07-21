package net.adhikary.mrtbuddy.ui.screens.farecalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import mrtbuddy.composeapp.generated.resources.Res
import mrtbuddy.composeapp.generated.resources.fare
import net.adhikary.mrtbuddy.model.CardState
import net.adhikary.mrtbuddy.ui.components.LiftingHeader
import net.adhikary.mrtbuddy.ui.screens.components.FareDisplayCard
import net.adhikary.mrtbuddy.ui.screens.components.StationSelectionSection
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FareCalculatorScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: FareCalculatorViewModel = koinViewModel(),
    cardState: CardState
) {
    val uiState = viewModel.state.collectAsState()

    // Update card state when it changes
    LaunchedEffect(cardState) {
        viewModel.onAction(FareCalculatorAction.UpdateCardState(cardState))
    }

    // Initialize ViewModel
    LaunchedEffect(Unit) {
        viewModel.onAction(FareCalculatorAction.OnInit)
    }

    // Handle events from ViewModel
    LaunchedEffect(viewModel.events) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is FareCalculatorEvent.Error -> {
                    // Handle error event
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().then(modifier)) {
        LiftingHeader(
            title = stringResource(Res.string.fare),
            lifted = false,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp + paddingValues.calculateBottomPadding(),
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FareDisplayCard(uiState.value, viewModel)
            StationSelectionSection(uiState.value, viewModel)
        }
    }
}
