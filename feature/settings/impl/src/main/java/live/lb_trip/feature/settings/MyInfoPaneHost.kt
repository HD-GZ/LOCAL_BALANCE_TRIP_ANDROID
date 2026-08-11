package live.lb_trip.feature.settings

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.component.LbEmptyDetailPane
import live.lb_trip.core.designsystem.component.LbSinglePaneBackHandler
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.designsystem.component.calculateLbListDetailDirective
import live.lb_trip.domain.model.TermsType

private enum class SettingsDetailTarget {
    PROFILE,
    LICENSES,
    TERMS,
    PRIVACY,
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MyInfoPaneHost(
    onNavigateToSavedCourses: () -> Unit,
    onNavigateToDiagnosis: () -> Unit,
    onNavigateToSignin: () -> Unit,
    @RawRes librariesRawResId: Int,
    modifier: Modifier = Modifier,
) {
    val directive = calculateLbListDetailDirective()
    val isTwoPane = directive.maxHorizontalPartitions > 1
    val navigator = rememberListDetailPaneScaffoldNavigator<SettingsDetailTarget>(scaffoldDirective = directive)
    val scope = rememberCoroutineScope()
    var profileUpdated by remember { mutableStateOf(false) }

    LbSinglePaneBackHandler(navigator = navigator, isTwoPane = isTwoPane)

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        modifier = modifier,
        listPane = {
            AnimatedPane {
                Column(modifier = Modifier.fillMaxSize()) {
                    LbTopBar(
                        onBackClick = {},
                        backContentDescription = "",
                        title = stringResource(R.string.settings_pane_title),
                        showBackButton = false,
                    )
                    MyInfoTabContent(
                        onNavigateToSavedCourses = onNavigateToSavedCourses,
                        onNavigateToDiagnosis = onNavigateToDiagnosis,
                        onNavigateToEditProfile = {
                            scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, SettingsDetailTarget.PROFILE) }
                        },
                        onNavigateToLicenses = {
                            scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, SettingsDetailTarget.LICENSES) }
                        },
                        onNavigateToTerms = {
                            scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, SettingsDetailTarget.TERMS) }
                        },
                        onNavigateToPrivacy = {
                            scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, SettingsDetailTarget.PRIVACY) }
                        },
                        onNavigateToSignin = onNavigateToSignin,
                        profileUpdated = profileUpdated,
                        onProfileUpdatedConsumed = { profileUpdated = false },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane {
                when (navigator.currentDestination?.contentKey) {
                    SettingsDetailTarget.PROFILE -> EditProfileScreen(
                        onBack = { scope.launch { navigator.navigateBack() } },
                        onSaved = {
                            profileUpdated = true
                            scope.launch { navigator.navigateBack() }
                        },
                        showBackButton = !isTwoPane,
                    )

                    SettingsDetailTarget.LICENSES -> LicensesScreen(
                        librariesRawResId = librariesRawResId,
                        onBack = { scope.launch { navigator.navigateBack() } },
                        showBackButton = !isTwoPane,
                    )

                    SettingsDetailTarget.TERMS -> TermsScreen(
                        type = TermsType.SERVICE,
                        fallbackTitle = stringResource(R.string.settings_menu_terms),
                        onBack = { scope.launch { navigator.navigateBack() } },
                        showBackButton = !isTwoPane,
                    )

                    SettingsDetailTarget.PRIVACY -> TermsScreen(
                        type = TermsType.PRIVACY,
                        fallbackTitle = stringResource(R.string.settings_menu_privacy),
                        onBack = { scope.launch { navigator.navigateBack() } },
                        showBackButton = !isTwoPane,
                    )

                    null -> LbEmptyDetailPane(message = stringResource(R.string.settings_select_prompt))
                }
            }
        },
    )
}
