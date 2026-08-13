package live.lb_trip.localbalancetrip

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneMotionDefaults
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomTabItem
import live.lb_trip.core.designsystem.component.LbBrandTopBar
import live.lb_trip.core.designsystem.component.LbNavigationSuiteScaffold
import live.lb_trip.feature.home.HomeTabContent
import live.lb_trip.feature.settings.MyInfoPaneHost

private enum class MainTab {
    HOME,
    MY_INFO,
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun MainTabScreen(
    isLoggedIn: Boolean,
    onStartDiagnosis: () -> Unit,
    onNavigateToSignin: () -> Unit,
    onNavigateToSavedCourseDetail: (Long) -> Unit,
    onSavedAllClick: () -> Unit,
    onNavigateToSavedCourses: () -> Unit,
    onRetakeDiagnosis: () -> Unit,
    onNavigateToPolicyList: () -> Unit,
    onNavigateToPopularCourseDetail: (Long) -> Unit,
    onNavigateToRecommendedRegion: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current
    val activity = LocalActivity.current
    val navSuiteState = rememberNavigationSuiteScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) selectedTab = MainTab.HOME
    }

    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    val mainTabLabel = stringResource(R.string.main_tab_main)
    val myInfoTabLabel = stringResource(R.string.main_tab_my_info)
    val brandPrefix = stringResource(R.string.main_brand_prefix)
    val brandHighlight = stringResource(R.string.main_brand_highlight)
    val brandSuffix = stringResource(R.string.main_brand_suffix)

    LbNavigationSuiteScaffold(
        modifier = modifier,
        state = navSuiteState,
        items = listOf(
            LbBottomTabItem(
                label = mainTabLabel,
                icon = Icons.Filled.Home,
                selected = selectedTab == MainTab.HOME,
                onClick = { selectedTab = MainTab.HOME },
            ),
            LbBottomTabItem(
                label = myInfoTabLabel,
                icon = Icons.Outlined.Person,
                selected = selectedTab == MainTab.MY_INFO,
                onClick = { selectedTab = MainTab.MY_INFO },
            ),
        ),
    ) {
        Scaffold(
            topBar = {
                if (selectedTab == MainTab.HOME) {
                    LbBrandTopBar(
                        title = buildAnnotatedString {
                            append(brandPrefix)
                            withStyle(SpanStyle(color = LbColors.Green)) { append(brandHighlight) }
                            append(brandSuffix)
                        },
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = LbColors.ScreenBg,
            // The outer LbNavigationSuiteScaffold already lays out its bar as a dedicated region
            // (content never sits under it), so this Scaffold never needs to reserve navigationBars
            // itself. It only relied on the nav suite's ambient-inset bookkeeping for that, which
            // does not reliably re-zero after the bar animates through hide()/show() — reserving it
            // here too then double-pads content with a stale, already-handled gap.
            // My Info additionally has no outer top bar of its own, so its status bar inset is left
            // untouched too, letting LbTopBar/each detail screen's own Scaffold paint behind it directly.
            contentWindowInsets = if (selectedTab == MainTab.MY_INFO) {
                WindowInsets(0, 0, 0, 0)
            } else {
                ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.navigationBars)
            },
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val forward = targetState.ordinal > initialState.ordinal
                    val enter = slideInHorizontally(PaneMotionDefaults.OffsetAnimationSpec) { width ->
                        if (forward) width else -width
                    } + fadeIn(PaneMotionDefaults.VisibilityAnimationSpec)
                    val exit = slideOutHorizontally(PaneMotionDefaults.OffsetAnimationSpec) { width ->
                        if (forward) -width else width
                    } + fadeOut(PaneMotionDefaults.VisibilityAnimationSpec)
                    enter.togetherWith(exit)
                },
                label = "MainTabContent",
            ) { tab ->
                when (tab) {
                    MainTab.HOME -> HomeTabContent(
                        onStartDiagnosisClick = onStartDiagnosis,
                        onNavigateToSignin = onNavigateToSignin,
                        onSavedAllClick = onSavedAllClick,
                        onCourseClick = onNavigateToSavedCourseDetail,
                        onPopularCourseClick = onNavigateToPopularCourseDetail,
                        onPolicyAllClick = onNavigateToPolicyList,
                        onNavigateToRecommendedRegion = onNavigateToRecommendedRegion,
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                    )

                    MainTab.MY_INFO -> MyInfoPaneHost(
                        onNavigateToSavedCourses = onNavigateToSavedCourses,
                        onNavigateToDiagnosis = onRetakeDiagnosis,
                        onNavigateToSignin = onNavigateToSignin,
                        librariesRawResId = R.raw.aboutlibraries,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding),
                        onDetailPaneVisibleChange = { isDetailShown ->
                            coroutineScope.launch {
                                if (isDetailShown) navSuiteState.hide() else navSuiteState.show()
                            }
                        },
                    )
                }
            }
        }
    }
}
