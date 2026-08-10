package live.lb_trip.localbalancetrip

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import live.lb_trip.core.designsystem.LocalBalanceTripTheme
import live.lb_trip.feature.home.HomeRoute
import live.lb_trip.feature.home.PolicyListRoute
import live.lb_trip.feature.home.policyListScreen
import live.lb_trip.feature.onboarding.onboardingScreen
import live.lb_trip.feature.propensity.PropensityRoute
import live.lb_trip.feature.propensity.propensityScreen
import live.lb_trip.feature.recommendation.CourseDetailRoute
import live.lb_trip.feature.recommendation.RecommendationRoute
import live.lb_trip.feature.recommendation.recommendationScreen
import live.lb_trip.feature.savedcourses.RECEIPT_REGISTERED_RESULT_KEY
import live.lb_trip.feature.savedcourses.ReceiptCaptureRoute
import live.lb_trip.feature.savedcourses.SavedCourseDetailRoute
import live.lb_trip.feature.savedcourses.SavedCoursesRoute
import live.lb_trip.feature.savedcourses.TOUR_ENDED_RESULT_KEY
import live.lb_trip.feature.savedcourses.receiptCaptureScreen
import live.lb_trip.feature.savedcourses.savedCourseDetailScreen
import live.lb_trip.feature.savedcourses.savedCoursesScreen
import live.lb_trip.feature.settings.EditProfileRoute
import live.lb_trip.feature.settings.LicensesRoute
import live.lb_trip.feature.settings.PROFILE_UPDATED_RESULT_KEY
import live.lb_trip.feature.settings.PrivacyRoute
import live.lb_trip.feature.settings.TermsRoute
import live.lb_trip.feature.settings.editProfileScreen
import live.lb_trip.feature.settings.licensesScreen
import live.lb_trip.feature.settings.privacyScreen
import live.lb_trip.feature.settings.termsScreen
import live.lb_trip.feature.signin.SigninRoute
import live.lb_trip.feature.signin.signinScreen
import live.lb_trip.feature.signup.SignupRoute
import live.lb_trip.feature.signup.signupScreen
import live.lb_trip.feature.tour.TourRoute
import live.lb_trip.feature.tour.tourScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.isLoggedIn == null }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val sessionExpiredMessage = stringResource(R.string.main_session_expired)

            LaunchedEffect(Unit) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        MainSideEffect.ShowSessionExpired -> {
                            Toast.makeText(context, sessionExpiredMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            LocalBalanceTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val isLoggedIn = uiState.isLoggedIn
                    if (isLoggedIn != null) {
                        MainNavGraph(isLoggedIn = isLoggedIn)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainNavGraph(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> { backStackEntry ->
            val profileUpdated by backStackEntry.savedStateHandle
                .getStateFlow(PROFILE_UPDATED_RESULT_KEY, false)
                .collectAsStateWithLifecycle()
            MainTabScreen(
                isLoggedIn = isLoggedIn,
                onStartDiagnosis = { navController.navigate(PropensityRoute()) },
                onNavigateToSignin = { navController.navigate(SigninRoute) },
                onNavigateToSavedCourseDetail = { savedCourseId ->
                    navController.navigate(SavedCourseDetailRoute(savedCourseId))
                },
                onSavedAllClick = { navController.navigate(SavedCoursesRoute) },
                onNavigateToSavedCourses = { navController.navigate(SavedCoursesRoute) },
                onRetakeDiagnosis = { navController.navigate(PropensityRoute(forceNew = true)) },
                onNavigateToEditProfile = { navController.navigate(EditProfileRoute) },
                onNavigateToLicenses = { navController.navigate(LicensesRoute) },
                onNavigateToTerms = { navController.navigate(TermsRoute) },
                onNavigateToPrivacy = { navController.navigate(PrivacyRoute) },
                onNavigateToPolicyList = { navController.navigate(PolicyListRoute) },
                onNavigateToCourseDetail = { courseId -> navController.navigate(CourseDetailRoute(courseId)) },
                profileUpdated = profileUpdated,
                onProfileUpdatedConsumed = {
                    backStackEntry.savedStateHandle[PROFILE_UPDATED_RESULT_KEY] = false
                },
            )
        }
        policyListScreen(onBack = navController::popBackStack)
        editProfileScreen(
            onBack = navController::popBackStack,
            onSaved = {
                navController.previousBackStackEntry?.savedStateHandle?.set(PROFILE_UPDATED_RESULT_KEY, true)
                navController.popBackStack()
            },
        )
        licensesScreen(
            librariesRawResId = R.raw.aboutlibraries,
            onBack = navController::popBackStack,
        )
        termsScreen(onBack = navController::popBackStack)
        privacyScreen(onBack = navController::popBackStack)
        savedCoursesScreen(
            onBack = navController::popBackStack,
            onCourseClick = { savedCourseId -> navController.navigate(SavedCourseDetailRoute(savedCourseId)) },
        )
        savedCourseDetailScreen(
            onBack = navController::popBackStack,
            onNavigateToTour = { savedCourseId -> navController.navigate(TourRoute(savedCourseId)) },
            onNavigateToReceiptCapture = { savedCourseId, imageUri ->
                navController.navigate(ReceiptCaptureRoute(savedCourseId, imageUri.toString()))
            },
        )
        receiptCaptureScreen(
            onBack = navController::popBackStack,
            onSubmitted = {
                navController.previousBackStackEntry?.savedStateHandle?.set(RECEIPT_REGISTERED_RESULT_KEY, true)
                navController.popBackStack()
            },
        )
        propensityScreen(
            navController = navController,
            onBack = navController::popBackStack,
            onNavigateToRecommendation = { navController.navigate(RecommendationRoute) },
            onNavigateToSignin = { navController.navigate(SigninRoute) },
        )
        recommendationScreen(
            navController = navController,
            onBack = navController::popBackStack,
        )
        tourScreen(
            onBack = navController::popBackStack,
            onTourFinished = {
                navController.previousBackStackEntry?.savedStateHandle?.set(TOUR_ENDED_RESULT_KEY, true)
                navController.popBackStack()
            },
        )
        signinScreen(
            navController = navController,
            onBack = navController::popBackStack,
            onNavigateToSignup = { navController.navigate(SignupRoute) },
        )
        signupScreen(
            navController = navController,
            onBack = navController::popBackStack,
            onNavigateToSignin = { navController.navigate(SigninRoute) },
        )
        onboardingScreen(
            onNavigateToSignup = { navController.navigate(SignupRoute) },
            onNavigateToSignin = { navController.navigate(SigninRoute) },
        )
    }
}
