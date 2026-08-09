package live.lb_trip.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.settings.components.SettingsLogoutGroup
import live.lb_trip.feature.settings.components.SettingsMenuGroup
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
    snackbarHostState: SnackbarHostState,
    profileUpdated: Boolean,
    onProfileUpdatedConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onIntent: (SettingsIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val loadErrorMessage = stringResource(R.string.settings_error_load)
    val retryActionLabel = stringResource(R.string.settings_action_retry)
    val unavailableTemplate = stringResource(R.string.settings_menu_unavailable_template)
    val profileUpdatedMessage = stringResource(R.string.settings_profile_updated)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                SettingsSideEffect.ShowLoadError -> launch {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(SettingsIntent.Retry)
                    }
                }

                is SettingsSideEffect.ShowUnavailableMessage -> launch {
                    snackbarHostState.showSnackbar(message = String.format(unavailableTemplate, effect.label))
                }

                SettingsSideEffect.NavigateToDiagnosis -> onNavigateToDiagnosis()
                SettingsSideEffect.NavigateToEditProfile -> onNavigateToEditProfile()
                SettingsSideEffect.NavigateToLicenses -> onNavigateToLicenses()
                SettingsSideEffect.NavigateToTerms -> onNavigateToTerms()
                SettingsSideEffect.NavigateToPrivacy -> onNavigateToPrivacy()
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

    MyInfoTabContentBody(
        state = state,
        onNavigateToSavedCourses = onNavigateToSavedCourses,
        onIntent = onIntent,
        modifier = modifier,
    )
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
            .verticalScroll(rememberScrollState()),
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
        } else {
            SettingsProfileHeader(
                name = state.name,
                email = state.email,
                onEditInfoClick = { onIntent(SettingsIntent.EditProfileClick) },
                modifier = Modifier.background(Color.White),
            )
            HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)

            SettingsSavedCoursesRow(
                count = state.savedCoursesCount,
                onClick = onNavigateToSavedCourses,
                modifier = Modifier.padding(top = 16.dp),
            )

            SettingsMenuGroup(
                label = stringResource(R.string.settings_group_label),
                items = listOf(
                    editInfoLabel to { onIntent(SettingsIntent.EditProfileClick) },
                    retakeDiagnosisLabel to { onIntent(SettingsIntent.RetakeDiagnosisClick) },
                    licensesLabel to { onIntent(SettingsIntent.LicensesClick) },
                    termsLabel to { onIntent(SettingsIntent.TermsClick) },
                    privacyLabel to { onIntent(SettingsIntent.PrivacyClick) },
                    contactLabel to { onIntent(SettingsIntent.MenuItemClick(contactLabel)) },
                ),
                versionName = versionName,
                modifier = Modifier.padding(top = 22.dp),
            )

            SettingsLogoutGroup(
                onLogoutClick = { onIntent(SettingsIntent.LogoutClick) },
                modifier = Modifier.padding(top = 14.dp),
            )

            Text(
                text = stringResource(R.string.settings_footer),
                color = LbColors.Ink4,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MyInfoTabContentBody(
        state = SettingsUiState(isLoading = false, name = "홍길동", email = "local@email.com", savedCoursesCount = 5),
        onNavigateToSavedCourses = {},
        onIntent = {},
    )
}
