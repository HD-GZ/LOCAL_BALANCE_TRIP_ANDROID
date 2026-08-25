package live.lb_trip.localbalancetrip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
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
import live.lb_trip.feature.home.PopularCourseDetailRoute
import live.lb_trip.feature.home.policyListScreen
import live.lb_trip.feature.home.popularCourseDetailScreen
import live.lb_trip.feature.propensity.PropensityRoute
import live.lb_trip.feature.propensity.propensityScreen
import live.lb_trip.feature.recommendation.CourseRoute
import live.lb_trip.feature.recommendation.RecommendationRoute
import live.lb_trip.feature.recommendation.recommendationScreen
import live.lb_trip.feature.savedcourses.RECEIPT_REGISTERED_RESULT_KEY
import live.lb_trip.feature.savedcourses.ReceiptCaptureRoute
import live.lb_trip.feature.savedcourses.ReceiptDetailRoute
import live.lb_trip.feature.savedcourses.SavedCoursesRoute
import live.lb_trip.feature.savedcourses.SharedCourseRoute
import live.lb_trip.feature.savedcourses.TOUR_ENDED_RESULT_KEY
import live.lb_trip.feature.savedcourses.TOUR_ENDED_SHOW_REPORT_RESULT_KEY
import live.lb_trip.feature.savedcourses.receiptCaptureScreen
import live.lb_trip.feature.savedcourses.receiptDetailScreen
import live.lb_trip.feature.savedcourses.savedCoursesScreen
import live.lb_trip.feature.savedcourses.sharedCourseDetailScreen
import live.lb_trip.feature.settings.TermsRoute
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
    private val pendingShareToken = mutableStateOf<String?>(null)
    private val pendingShortcutRoute = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.isLoggedIn == null }
        pendingShareToken.value = intent.extractShareToken()
        pendingShortcutRoute.value = intent.extractShortcutRoute()

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
                        MainNavGraph(
                            isLoggedIn = isLoggedIn,
                            pendingShareToken = pendingShareToken.value,
                            onShareTokenConsumed = { pendingShareToken.value = null },
                            pendingShortcutRoute = pendingShortcutRoute.value,
                            onShortcutRouteConsumed = { pendingShortcutRoute.value = null },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingShareToken.value = intent.extractShareToken()
        pendingShortcutRoute.value = intent.extractShortcutRoute()
    }
}

private const val EXTRA_SHORTCUT_ROUTE = "shortcut_route"
private const val SHORTCUT_ROUTE_SAVED_COURSES = "saved_courses"
private const val SHORTCUT_ROUTE_POLICY_LIST = "policy_list"

private fun Intent.extractShareToken(): String? {
    val uri = data ?: return null
    Log.d("MainActivity", "Received deep link: $uri")
    return uri.getQueryParameter("token")
}

private fun Intent.extractShortcutRoute(): String? = getStringExtra(EXTRA_SHORTCUT_ROUTE)

@Composable
private fun MainNavGraph(
    isLoggedIn: Boolean,
    pendingShareToken: String? = null,
    onShareTokenConsumed: () -> Unit = {},
    pendingShortcutRoute: String? = null,
    onShortcutRouteConsumed: () -> Unit = {},
) {
    val navController = rememberNavController()

    LaunchedEffect(pendingShareToken) {
        if (pendingShareToken != null) {
            navController.navigate(SharedCourseRoute(pendingShareToken))
            onShareTokenConsumed()
        }
    }

    LaunchedEffect(pendingShortcutRoute) {
        when (pendingShortcutRoute) {
            SHORTCUT_ROUTE_SAVED_COURSES -> navController.navigate(SavedCoursesRoute())
            SHORTCUT_ROUTE_POLICY_LIST -> navController.navigate(PolicyListRoute)
        }
        if (pendingShortcutRoute != null) {
            onShortcutRouteConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        enterTransition = appEnterTransition,
        exitTransition = appExitTransition,
        popEnterTransition = appPopEnterTransition,
        popExitTransition = appPopExitTransition,
    ) {
        composable<HomeRoute> {
            MainTabScreen(
                isLoggedIn = isLoggedIn,
                onStartDiagnosis = { navController.navigate(PropensityRoute()) },
                onNavigateToSignin = { navController.navigate(SigninRoute) },
                onNavigateToSavedCourseDetail = { savedCourseId ->
                    navController.navigate(SavedCoursesRoute(initialSavedCourseId = savedCourseId))
                },
                onSavedAllClick = { navController.navigate(SavedCoursesRoute()) },
                onNavigateToSavedCourses = { navController.navigate(SavedCoursesRoute()) },
                onRetakeDiagnosis = { navController.navigate(PropensityRoute(forceNew = true)) },
                onNavigateToPolicyList = { navController.navigate(PolicyListRoute) },
                onNavigateToPopularCourseDetail = { courseId -> navController.navigate(PopularCourseDetailRoute(courseId)) },
                onNavigateToRecommendedRegion = { regionId, regionName ->
                    navController.navigate(CourseRoute(regionId = regionId, regionName = regionName))
                },
            )
        }
        policyListScreen(onBack = navController::popBackStack)
        popularCourseDetailScreen(onBack = navController::popBackStack)
        savedCoursesScreen(
            onBack = navController::popBackStack,
            onNavigateToTour = { savedCourseId -> navController.navigate(TourRoute(savedCourseId)) },
            onNavigateToReceiptCapture = { savedCourseId, imageUri ->
                navController.navigate(ReceiptCaptureRoute(savedCourseId, imageUri.toString()))
            },
            onNavigateToReceiptDetail = { savedCourseId, receiptId ->
                navController.navigate(ReceiptDetailRoute(savedCourseId, receiptId))
            },
        )
        receiptCaptureScreen(
            onBack = navController::popBackStack,
            onSubmitted = {
                navController.previousBackStackEntry?.savedStateHandle?.set(RECEIPT_REGISTERED_RESULT_KEY, true)
                navController.popBackStack()
            },
        )
        receiptDetailScreen(
            onBack = navController::popBackStack,
            onUpdated = {
                navController.previousBackStackEntry?.savedStateHandle?.set(RECEIPT_REGISTERED_RESULT_KEY, true)
            },
            onDeleted = {
                navController.previousBackStackEntry?.savedStateHandle?.set(RECEIPT_REGISTERED_RESULT_KEY, true)
                navController.popBackStack()
            },
        )
        sharedCourseDetailScreen(onBack = navController::popBackStack)
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
            onTourFinished = { showReport ->
                navController.previousBackStackEntry?.savedStateHandle?.set(TOUR_ENDED_RESULT_KEY, true)
                if (showReport) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(TOUR_ENDED_SHOW_REPORT_RESULT_KEY, true)
                }
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
            onNavigateToSignin = {
                navController.navigate(SigninRoute) {
                    popUpTo(HomeRoute) { inclusive = false }
                }
            },
            onNavigateToTerms = { type -> navController.navigate(TermsRoute(type.name)) },
        )
        termsScreen(navController = navController)
    }
}
