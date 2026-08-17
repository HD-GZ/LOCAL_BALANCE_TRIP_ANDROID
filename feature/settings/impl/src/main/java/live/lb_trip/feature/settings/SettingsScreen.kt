package live.lb_trip.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.settings.components.SettingsAuthPromptDialog
import live.lb_trip.feature.settings.components.SettingsGuestProfileHeader
import live.lb_trip.feature.settings.components.SettingsLogoutGroup
import live.lb_trip.feature.settings.components.SettingsMenuGroup
import live.lb_trip.feature.settings.components.SettingsMenuItem
import live.lb_trip.feature.settings.components.SettingsProfileHeader
import live.lb_trip.feature.settings.components.SettingsSavedCoursesRow

@Composable
fun MyInfoTabContent(
    onNavigateToSavedCourses: () -> Unit,
    onNavigateToDiagnosis: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToSignin: () -> Unit,
    profileUpdated: Boolean,
    onProfileUpdatedConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onIntent: (SettingsIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.settings_error_load)
    val retryActionLabel = stringResource(R.string.settings_action_retry)
    val profileUpdatedMessage = stringResource(R.string.settings_profile_updated)
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                SettingsSideEffect.ShowLoadError -> launch {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(SettingsIntent.Retry)
                    }
                }

                SettingsSideEffect.NavigateToDiagnosis -> onNavigateToDiagnosis()
                SettingsSideEffect.NavigateToEditProfile -> onNavigateToEditProfile()
                SettingsSideEffect.NavigateToLicenses -> onNavigateToLicenses()
                SettingsSideEffect.NavigateToTerms -> onNavigateToTerms()
                SettingsSideEffect.NavigateToPrivacy -> onNavigateToPrivacy()
                SettingsSideEffect.NavigateToSignin -> onNavigateToSignin()
                SettingsSideEffect.OpenContactEmail -> uriHandler.openUri("mailto:$CONTACT_EMAIL")
                SettingsSideEffect.ShowProfileUpdated -> launch { snackbarHostState.showSnackbar(profileUpdatedMessage) }
            }
        }
    }

    LaunchedEffect(profileUpdated) {
        if (profileUpdated) {
            onIntent(SettingsIntent.ProfileUpdated)
            onProfileUpdatedConsumed()
        }
    }

    Box(modifier = modifier) {
        MyInfoTabContentBody(
            state = state,
            onNavigateToSavedCourses = onNavigateToSavedCourses,
            onIntent = onIntent,
            modifier = Modifier.fillMaxSize(),
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (state.showAuthPrompt) {
        SettingsAuthPromptDialog(
            onConfirm = { onIntent(SettingsIntent.ConfirmAuthPrompt) },
            onDismiss = { onIntent(SettingsIntent.DismissAuthPrompt) },
        )
    }
}

@Composable
private fun MyInfoTabContentBody(
    state: SettingsUiState,
    onNavigateToSavedCourses: () -> Unit,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val versionName = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull()
            .orEmpty()
    }
    val editInfoLabel = stringResource(R.string.settings_edit_info)
    val retakeDiagnosisLabel = stringResource(R.string.settings_menu_retake_diagnosis)
    val licensesLabel = stringResource(R.string.settings_menu_licenses)
    val termsLabel = stringResource(R.string.settings_menu_terms)
    val privacyLabel = stringResource(R.string.settings_menu_privacy)
    val contactLabel = stringResource(R.string.settings_menu_contact)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 22.dp),
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
        } else {
            if (state.isLoggedIn) {
                SettingsProfileHeader(
                    name = state.name,
                    email = state.email,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 6.dp),
                )
                SettingsSavedCoursesRow(
                    count = state.savedCoursesCount,
                    onClick = onNavigateToSavedCourses,
                )
            } else {
                SettingsGuestProfileHeader(
                    onLoginClick = { onIntent(SettingsIntent.GuestLoginClick) },
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 6.dp),
                )
            }

            SettingsMenuGroup(
                items = listOf(
                    SettingsMenuItem(editInfoLabel) { onIntent(SettingsIntent.EditProfileClick) },
                    SettingsMenuItem(retakeDiagnosisLabel) { onIntent(SettingsIntent.RetakeDiagnosisClick) },
                    SettingsMenuItem(licensesLabel) { onIntent(SettingsIntent.LicensesClick) },
                    SettingsMenuItem(termsLabel) { onIntent(SettingsIntent.TermsClick) },
                    SettingsMenuItem(privacyLabel) { onIntent(SettingsIntent.PrivacyClick) },
                    SettingsMenuItem(contactLabel) { onIntent(SettingsIntent.ContactClick) },
                ),
                versionName = versionName,
                modifier = Modifier.padding(top = 10.dp),
            )

            if (state.isLoggedIn) {
                SettingsLogoutGroup(
                    onLogoutClick = { onIntent(SettingsIntent.LogoutClick) },
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }
    }
}

private const val CONTACT_EMAIL = "hdgz@lb-trip.live"

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MyInfoTabContentBody(
        state = SettingsUiState(isLoading = false, isLoggedIn = true, name = "홍길동", email = "local@email.com", savedCoursesCount = 5),
        onNavigateToSavedCourses = {},
        onIntent = {},
    )
}
