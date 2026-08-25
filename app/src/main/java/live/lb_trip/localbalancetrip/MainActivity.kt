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
import live.lb_trip.feature.home.HOME_REFRESH_RESULT_KEY
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
import live.lb_trip.feature.savedcourses.SAVED_COURSES_REFRESH_RESULT_KEY
import live.lb_trip.feature.savedcourses.ReceiptCaptureRoute
import live.lb_trip.feature.savedcourses.ReceiptDetailRoute
import live.lb_trip.feature.savedcourses.SavedCoursesRoute
import live.lb_trip.feature.savedcourses.SharedCourseRoute
import live.lb_trip.feature.savedcourses.TOUR_ENDED_RESULT_KEY
import live.lb_trip.feature.savedcourses.TOUR_ENDED_SHOW_REPORT_RESULT_KEY
import live.lb_trip.feature.savedcourses.TOUR_STARTED_RESULT_KEY
import live.lb_trip.feature.savedcourses.receiptCaptureScreen
import live.lb_trip.feature.savedcourses.receiptDetailScreen
import live.lb_trip.feature.savedcourses.savedCoursesScreen
import live.lb_trip.feature.savedcourses.sharedCourseDetailScreen
import live.lb_trip.feature.settings.MY_INFO_REFRESH_RESULT_KEY
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

    // 홈 밖에서 진단/코스 저장을 마치고 돌아왔을 때 홈이 다시 로드하도록 남기는 신호.
    // HomeRoute 는 startDestination 이라 항상 백스택에 있지만, entry 는 NavHost 가 만든 뒤에야
    // 존재하므로 반드시 콜백 안에서 조회한다.
    val markHomeNeedsRefresh = {
        navController.getBackStackEntry<HomeRoute>().savedStateHandle[HOME_REFRESH_RESULT_KEY] = true
    }
    // 코스 저장은 홈 피드와 '나의 정보'의 저장 코스 개수를 동시에 바꾼다. 두 탭은 서로 다른
    // 시점에 합성되므로 신호를 하나로 공유하면 먼저 합성된 쪽이 소비해 버린다 - 키를 나눈다.
    val markCourseSaved = {
        markHomeNeedsRefresh()
        navController.getBackStackEntry<HomeRoute>().savedStateHandle[MY_INFO_REFRESH_RESULT_KEY] = true
    }
    // 여행 시작/종료는 코스의 여행 상태를 바꾼다. 저장한 코스의 목록/상세 두 창도 소비자가
    // 달라 키를 나눠 둔다. 이 시점에는 여행 화면이 위에 있어 아래 화면들은 합성돼 있지 않다.
    val markTourStateChanged = {
        markHomeNeedsRefresh()
        navController.previousBackStackEntry?.savedStateHandle?.set(SAVED_COURSES_REFRESH_RESULT_KEY, true)
    }

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        enterTransition = appEnterTransition,
        exitTransition = appExitTransition,
        popEnterTransition = appPopEnterTransition,
        popExitTransition = appPopExitTransition,
    ) {
        composable<HomeRoute> { backStackEntry ->
            val homeNeedsRefresh by backStackEntry.savedStateHandle
                .getStateFlow(HOME_REFRESH_RESULT_KEY, false)
                .collectAsStateWithLifecycle()
            val myInfoNeedsRefresh by backStackEntry.savedStateHandle
                .getStateFlow(MY_INFO_REFRESH_RESULT_KEY, false)
                .collectAsStateWithLifecycle()
            MainTabScreen(
                isLoggedIn = isLoggedIn,
                homeNeedsRefresh = homeNeedsRefresh,
                onHomeRefreshConsumed = { backStackEntry.savedStateHandle[HOME_REFRESH_RESULT_KEY] = false },
                myInfoNeedsRefresh = myInfoNeedsRefresh,
                onMyInfoRefreshConsumed = { backStackEntry.savedStateHandle[MY_INFO_REFRESH_RESULT_KEY] = false },
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
            onDiagnosisSubmitted = markHomeNeedsRefresh,
        )
        recommendationScreen(
            navController = navController,
            onBack = navController::popBackStack,
            onCourseSaved = markCourseSaved,
        )
        tourScreen(
            onBack = navController::popBackStack,
            onTourFinished = { showReport ->
                navController.previousBackStackEntry?.savedStateHandle?.set(TOUR_ENDED_RESULT_KEY, true)
                if (showReport) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(TOUR_ENDED_SHOW_REPORT_RESULT_KEY, true)
                }
                markTourStateChanged()
                navController.popBackStack()
            },
            onTourStarted = {
                navController.previousBackStackEntry?.savedStateHandle?.set(TOUR_STARTED_RESULT_KEY, true)
                markTourStateChanged()
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
