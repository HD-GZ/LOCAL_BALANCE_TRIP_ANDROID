package live.lb_trip.feature.signin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import live.lb_trip.core.designsystem.component.LbLoadingOverlay

fun NavGraphBuilder.signinScreen(
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
) {
    navigation<SigninRoute>(startDestination = SigninHomeRoute) {
        composable<SigninHomeRoute> {
            SigninScreen(
                onBack = onBack,
                onNavigateToSignup = onNavigateToSignup,
                onNavigateToPasswordReset = { navController.navigate(PasswordResetRoute) },
            )
        }
        passwordResetGraph(navController)
    }
}

private fun NavGraphBuilder.passwordResetGraph(navController: NavController) {
    navigation<PasswordResetRoute>(startDestination = PasswordResetEmailRoute) {
        composable<PasswordResetEmailRoute> { entry ->
            PasswordResetEmailDestination(navController, entry)
        }
        composable<PasswordResetVerifyRoute> { entry ->
            PasswordResetVerifyDestination(navController, entry)
        }
        composable<PasswordResetNewPasswordRoute> { entry ->
            PasswordResetNewPasswordDestination(navController, entry)
        }
        composable<PasswordResetCompleteRoute> { entry ->
            PasswordResetCompleteDestination(navController, entry)
        }
    }
}

@Composable
private fun PasswordResetEmailDestination(navController: NavController, entry: NavBackStackEntry) {
    val viewModel = passwordResetSharedViewModel(navController, entry)
    PasswordResetStepScaffold(
        viewModel = viewModel,
        navController = navController,
        onNavigateToVerify = { navController.navigate(PasswordResetVerifyRoute) },
    ) { state ->
        PasswordResetEmailScreen(
            state = state,
            onBack = navController::popBackStack,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun PasswordResetVerifyDestination(navController: NavController, entry: NavBackStackEntry) {
    val viewModel = passwordResetSharedViewModel(navController, entry)
    DisposableEffect(Unit) {
        onDispose { viewModel.onIntent(PasswordResetIntent.VerifyStepLeft) }
    }
    PasswordResetStepScaffold(
        viewModel = viewModel,
        navController = navController,
        onNavigateToNewPassword = { navController.navigate(PasswordResetNewPasswordRoute) },
    ) { state ->
        PasswordResetVerifyScreen(
            state = state,
            onBack = navController::popBackStack,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun PasswordResetNewPasswordDestination(navController: NavController, entry: NavBackStackEntry) {
    val viewModel = passwordResetSharedViewModel(navController, entry)
    PasswordResetStepScaffold(
        viewModel = viewModel,
        navController = navController,
        onNavigateToComplete = { navController.navigate(PasswordResetCompleteRoute) },
    ) { state ->
        PasswordResetNewPasswordScreen(
            state = state,
            onBack = navController::popBackStack,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun PasswordResetCompleteDestination(navController: NavController, entry: NavBackStackEntry) {
    val viewModel = passwordResetSharedViewModel(navController, entry)
    BackHandler {
        navController.popBackStack(route = SigninHomeRoute, inclusive = false)
    }
    PasswordResetStepScaffold(
        viewModel = viewModel,
        navController = navController,
    ) { _ ->
        PasswordResetCompleteScreen(
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun passwordResetSharedViewModel(navController: NavController, entry: NavBackStackEntry): PasswordResetViewModel {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry(PasswordResetRoute)
    }
    return hiltViewModel(parentEntry)
}

@Composable
private fun PasswordResetStepScaffold(
    viewModel: PasswordResetViewModel,
    navController: NavController,
    onNavigateToVerify: () -> Unit = {},
    onNavigateToNewPassword: () -> Unit = {},
    onNavigateToComplete: () -> Unit = {},
    content: @Composable (PasswordResetUiState) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                PasswordResetSideEffect.NavigateToVerify -> onNavigateToVerify()
                PasswordResetSideEffect.NavigateToNewPassword -> onNavigateToNewPassword()
                PasswordResetSideEffect.NavigateToComplete -> onNavigateToComplete()
                PasswordResetSideEffect.NavigateToSignin ->
                    navController.popBackStack(route = SigninHomeRoute, inclusive = false)
                PasswordResetSideEffect.ShowCodeResent ->
                    snackbarHostState.showSnackbar("인증 코드를 다시 보냈어요.")
            }
        }
    }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        viewModel.onIntent(PasswordResetIntent.ErrorMessageConsumed)
        snackbarHostState.showSnackbar(message)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        content(state)

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
        )

        if (state.isLoading) {
            LbLoadingOverlay()
        }
    }
}
