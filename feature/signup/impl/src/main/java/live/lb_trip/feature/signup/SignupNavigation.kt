package live.lb_trip.feature.signup

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.domain.model.TermsType

fun NavGraphBuilder.signupScreen(
    navController: NavController,
    onBack: () -> Unit,
    onNavigateToSignin: () -> Unit,
    onNavigateToTerms: (TermsType) -> Unit,
) {
    navigation<SignupRoute>(startDestination = AccountInfoRoute) {
        composable<AccountInfoRoute> { entry ->
            AccountInfoStepDestination(navController, entry, onBack, onNavigateToSignin)
        }
        composable<PersonalInfoRoute> { entry ->
            PersonalInfoStepDestination(navController, entry, onNavigateToSignin, onNavigateToTerms)
        }
        composable<EmailVerifyRoute> { entry ->
            EmailVerifyStepDestination(navController, entry, onNavigateToSignin)
        }
        composable<CompleteRoute> { entry ->
            CompleteStepDestination(navController, entry, onNavigateToSignin)
        }
    }
}

@Composable
private fun AccountInfoStepDestination(
    navController: NavController,
    entry: NavBackStackEntry,
    onBack: () -> Unit,
    onNavigateToSignin: () -> Unit,
) {
    val viewModel = signupSharedViewModel(navController, entry)
    SignupStepScaffold(
        viewModel = viewModel,
        onNavigateToSignin = onNavigateToSignin,
        onNavigateToPersonalInfo = { navController.navigate(PersonalInfoRoute) },
    ) { state ->
        SignupAccountInfoScreen(
            state = state,
            onBack = onBack,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun PersonalInfoStepDestination(
    navController: NavController,
    entry: NavBackStackEntry,
    onNavigateToSignin: () -> Unit,
    onNavigateToTerms: (TermsType) -> Unit,
) {
    val viewModel = signupSharedViewModel(navController, entry)
    SignupStepScaffold(
        viewModel = viewModel,
        onNavigateToSignin = onNavigateToSignin,
        onNavigateToEmailVerify = { navController.navigate(EmailVerifyRoute) },
    ) { state ->
        SignupPersonalInfoScreen(
            state = state,
            onBack = navController::popBackStack,
            onIntent = viewModel::onIntent,
            onNavigateToTerms = onNavigateToTerms,
        )
    }
}

@Composable
private fun EmailVerifyStepDestination(
    navController: NavController,
    entry: NavBackStackEntry,
    onNavigateToSignin: () -> Unit,
) {
    val viewModel = signupSharedViewModel(navController, entry)
    DisposableEffect(Unit) {
        onDispose { viewModel.onIntent(SignupIntent.EmailVerifyStepLeft) }
    }
    SignupStepScaffold(
        viewModel = viewModel,
        onNavigateToSignin = onNavigateToSignin,
        onNavigateToComplete = { navController.navigate(CompleteRoute) },
    ) { state ->
        SignupEmailVerifyScreen(
            state = state,
            onBack = navController::popBackStack,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun CompleteStepDestination(
    navController: NavController,
    entry: NavBackStackEntry,
    onNavigateToSignin: () -> Unit,
) {
    val viewModel = signupSharedViewModel(navController, entry)
    SignupStepScaffold(
        viewModel = viewModel,
        onNavigateToSignin = onNavigateToSignin,
    ) { state ->
        SignupCompleteScreen(
            state = state,
            onIntent = viewModel::onIntent,
        )
    }
}

@Composable
private fun signupSharedViewModel(navController: NavController, entry: NavBackStackEntry): SignupViewModel {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry(SignupRoute)
    }
    return hiltViewModel(parentEntry)
}

@Composable
private fun SignupStepScaffold(
    viewModel: SignupViewModel,
    onNavigateToSignin: () -> Unit,
    onNavigateToPersonalInfo: () -> Unit = {},
    onNavigateToEmailVerify: () -> Unit = {},
    onNavigateToComplete: () -> Unit = {},
    content: @Composable (SignupUiState) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                SignupSideEffect.NavigateToSignin -> onNavigateToSignin()
                SignupSideEffect.NavigateToPersonalInfo -> onNavigateToPersonalInfo()
                SignupSideEffect.NavigateToEmailVerify -> onNavigateToEmailVerify()
                SignupSideEffect.NavigateToComplete -> onNavigateToComplete()
            }
        }
    }

    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
        }
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
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
        )

        if (state.isLoading) {
            LbLoadingOverlay()
        }
    }
}
