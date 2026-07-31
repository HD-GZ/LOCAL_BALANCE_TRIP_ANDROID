package live.lb_trip.localbalancetrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import live.lb_trip.core.designsystem.LocalBalanceTripTheme
import live.lb_trip.feature.home.HomeRoute
import live.lb_trip.feature.home.homeScreen
import live.lb_trip.feature.onboarding.OnboardingRoute
import live.lb_trip.feature.onboarding.onboardingScreen
import live.lb_trip.feature.propensity.PropensityRoute
import live.lb_trip.feature.propensity.propensityScreen
import live.lb_trip.feature.recommendation.RecommendationRoute
import live.lb_trip.feature.recommendation.recommendationScreen
import live.lb_trip.feature.savedcourses.RECEIPT_REGISTERED_RESULT_KEY
import live.lb_trip.feature.savedcourses.ReceiptCaptureRoute
import live.lb_trip.feature.savedcourses.SavedCourseDetailRoute
import live.lb_trip.feature.savedcourses.SavedCoursesRoute
import live.lb_trip.feature.savedcourses.receiptCaptureScreen
import live.lb_trip.feature.savedcourses.savedCourseDetailScreen
import live.lb_trip.feature.savedcourses.savedCoursesScreen
import live.lb_trip.feature.settings.SettingsRoute
import live.lb_trip.feature.settings.settingsScreen
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

        splashScreen.setKeepOnScreenCondition { viewModel.isLoggedIn.value == null }

        setContent {
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

            LocalBalanceTripTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when (isLoggedIn) {
                        true -> MainNavGraph()
                        false -> AuthNavGraph()
                        null -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun MainNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        homeScreen(
            onStartDiagnosis = { navController.navigate(PropensityRoute) },
            onNavigateToSettings = { navController.navigate(SettingsRoute) },
            onNavigateToSavedCourseDetail = { savedCourseId ->
                navController.navigate(SavedCourseDetailRoute(savedCourseId))
            },
            onSavedAllClick = { navController.navigate(SavedCoursesRoute) },
        )
        savedCoursesScreen(
            onBack = navController::popBackStack,
            onCourseClick = { savedCourseId -> navController.navigate(SavedCourseDetailRoute(savedCourseId)) },
        )
        savedCourseDetailScreen(
            onBack = navController::popBackStack,
            onNavigateToTour = { savedCourseId -> navController.navigate(TourRoute(savedCourseId)) },
            onNavigateToReceiptCapture = { savedCourseId ->
                navController.navigate(ReceiptCaptureRoute(savedCourseId))
            },
        )
        receiptCaptureScreen(
            onBack = navController::popBackStack,
            onSubmitted = {
                navController.previousBackStackEntry?.savedStateHandle?.set(RECEIPT_REGISTERED_RESULT_KEY, true)
                navController.popBackStack()
            },
        )
        settingsScreen(
            onNavigateToMain = navController::popBackStack,
            onNavigateToSavedCourses = { navController.navigate(SavedCoursesRoute) },
        )
        propensityScreen(
            navController = navController,
            onBack = navController::popBackStack,
            onNavigateToRecommendation = { navController.navigate(RecommendationRoute) },
        )
        recommendationScreen(
            navController = navController,
            onBack = navController::popBackStack,
        )
        tourScreen(onBack = navController::popBackStack)
    }
}

@Composable
private fun AuthNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = OnboardingRoute) {
        signinScreen(
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
