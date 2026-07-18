package live.lb_trip.feature.propensity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

fun NavGraphBuilder.propensityScreen(
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToRecommendation: () -> Unit,
) {
    navigation<PropensityRoute>(startDestination = PreferenceRoute) {
        composable<PreferenceRoute> { entry ->
            PreferenceStepDestination(navController = navController, entry = entry, onBack = onBack)
        }
        composable<ValueConsumptionRoute> { entry ->
            ValueConsumptionStepDestination(navController = navController, entry = entry)
        }
        composable<ResultRoute> { entry ->
            ResultStepDestination(
                navController = navController,
                entry = entry,
                onNavigateToRecommendation = onNavigateToRecommendation,
            )
        }
    }
}

@Composable
private fun PreferenceStepDestination(navController: NavController, entry: NavBackStackEntry, onBack: () -> Unit) {
    val viewModel = propensitySharedViewModel(navController, entry)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PropensityPreferenceScreen(
        state = state,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        onNextStep = { navController.navigate(ValueConsumptionRoute) },
    )
}

@Composable
private fun ValueConsumptionStepDestination(navController: NavController, entry: NavBackStackEntry) {
    val viewModel = propensitySharedViewModel(navController, entry)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            if (sideEffect == PropensitySideEffect.NavigateToResult) {
                navController.navigate(ResultRoute)
            }
        }
    }

    PropensityValueConsumptionScreen(
        state = state,
        onBack = navController::popBackStack,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun ResultStepDestination(
    navController: NavController,
    entry: NavBackStackEntry,
    onNavigateToRecommendation: () -> Unit,
) {
    val viewModel = propensitySharedViewModel(navController, entry)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                PropensitySideEffect.NavigateToRecommendation -> onNavigateToRecommendation()
                PropensitySideEffect.RestartToPreference -> {
                    navController.navigate(PreferenceRoute) {
                        popUpTo(PreferenceRoute) { inclusive = true }
                    }
                }
                PropensitySideEffect.NavigateToResult -> Unit
            }
        }
    }

    PropensityResultScreen(
        state = state,
        onBack = navController::popBackStack,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun propensitySharedViewModel(navController: NavController, entry: NavBackStackEntry): PropensityViewModel {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry(PropensityRoute)
    }
    return hiltViewModel(parentEntry)
}
