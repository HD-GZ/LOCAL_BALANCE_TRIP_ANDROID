package live.lb_trip.localbalancetrip

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.view.WindowCompat
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomTabItem
import live.lb_trip.core.designsystem.component.LbBrandTopBar
import live.lb_trip.core.designsystem.component.LbMainBottomBar
import live.lb_trip.feature.home.HomeTabContent
import live.lb_trip.feature.settings.MyInfoTabContent

private enum class MainTab {
    HOME,
    MY_INFO,
}

@Composable
internal fun MainTabScreen(
    isLoggedIn: Boolean,
    onStartDiagnosis: () -> Unit,
    onNavigateToSignin: () -> Unit,
    onNavigateToSavedCourseDetail: (Long) -> Unit,
    onSavedAllClick: () -> Unit,
    onNavigateToSavedCourses: () -> Unit,
    onRetakeDiagnosis: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToPolicyList: () -> Unit,
    onNavigateToPopularCourseDetail: (Long) -> Unit,
    onNavigateToRecommendedRegion: (Long, String) -> Unit,
    profileUpdated: Boolean,
    onProfileUpdatedConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current
    val activity = LocalActivity.current

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

    Scaffold(
        modifier = modifier,
        topBar = {
            LbBrandTopBar(
                title = buildAnnotatedString {
                    append(brandPrefix)
                    withStyle(SpanStyle(color = LbColors.Green)) { append(brandHighlight) }
                    append(brandSuffix)
                },
            )
        },
        bottomBar = {
            LbMainBottomBar(
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
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LbColors.ScreenBg,
    ) { innerPadding ->
        when (selectedTab) {
            MainTab.HOME -> HomeTabContent(
                onStartDiagnosisClick = onStartDiagnosis,
                onNavigateToSignin = onNavigateToSignin,
                onSavedAllClick = onSavedAllClick,
                onCourseClick = onNavigateToSavedCourseDetail,
                onPopularCourseClick = onNavigateToPopularCourseDetail,
                onPolicyAllClick = onNavigateToPolicyList,
                onNavigateToRecommendedRegion = onNavigateToRecommendedRegion,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(innerPadding),
            )

            MainTab.MY_INFO -> MyInfoTabContent(
                onNavigateToSavedCourses = onNavigateToSavedCourses,
                onNavigateToDiagnosis = onRetakeDiagnosis,
                onNavigateToEditProfile = onNavigateToEditProfile,
                onNavigateToLicenses = onNavigateToLicenses,
                onNavigateToTerms = onNavigateToTerms,
                onNavigateToPrivacy = onNavigateToPrivacy,
                onNavigateToSignin = onNavigateToSignin,
                profileUpdated = profileUpdated,
                onProfileUpdatedConsumed = onProfileUpdatedConsumed,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
