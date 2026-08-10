package live.lb_trip.feature.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.ClearDistanceRecordingUseCase
import live.lb_trip.domain.usecase.ClearSessionUseCase
import live.lb_trip.domain.usecase.GetSavedCoursesUseCase
import live.lb_trip.domain.usecase.GetTokensUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase
import live.lb_trip.domain.usecase.LogoutUseCase

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getTokensUseCase: GetTokensUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getSavedCoursesUseCase: GetSavedCoursesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val clearDistanceRecordingUseCase: ClearDistanceRecordingUseCase,
) : BaseViewModel<SettingsUiState, SettingsIntent, SettingsSideEffect>(SettingsUiState()) {

    init {
        viewModelScope.launch {
            getTokensUseCase()
                .map { it != null }
                .distinctUntilChanged()
                .collect { loggedIn -> load(loggedIn) }
        }
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Retry -> viewModelScope.launch { load(currentState.isLoggedIn) }
            is SettingsIntent.MenuItemClick -> postSideEffect(SettingsSideEffect.ShowUnavailableMessage(intent.label))
            SettingsIntent.EditProfileClick -> handleAuthGatedClick(SettingsSideEffect.NavigateToEditProfile)
            SettingsIntent.RetakeDiagnosisClick -> handleAuthGatedClick(SettingsSideEffect.NavigateToDiagnosis)
            SettingsIntent.LicensesClick -> postSideEffect(SettingsSideEffect.NavigateToLicenses)
            SettingsIntent.TermsClick -> postSideEffect(SettingsSideEffect.NavigateToTerms)
            SettingsIntent.PrivacyClick -> postSideEffect(SettingsSideEffect.NavigateToPrivacy)
            SettingsIntent.ProfileUpdated -> viewModelScope.launch {
                load(currentState.isLoggedIn)
                postSideEffect(SettingsSideEffect.ShowProfileUpdated)
            }
            SettingsIntent.LogoutClick -> viewModelScope.launch { logout() }
            SettingsIntent.GuestLoginClick -> postSideEffect(SettingsSideEffect.NavigateToSignin)
            SettingsIntent.DismissAuthPrompt -> updateState { it.copy(showAuthPrompt = false) }
            SettingsIntent.ConfirmAuthPrompt -> {
                updateState { it.copy(showAuthPrompt = false) }
                postSideEffect(SettingsSideEffect.NavigateToSignin)
            }
        }
    }

    private fun handleAuthGatedClick(sideEffect: SettingsSideEffect) {
        if (currentState.isLoggedIn) {
            postSideEffect(sideEffect)
        } else {
            updateState { it.copy(showAuthPrompt = true) }
        }
    }

    private suspend fun load(loggedIn: Boolean) {
        if (!loggedIn) {
            updateState {
                it.copy(isLoading = false, isLoggedIn = false, name = "", email = "", savedCoursesCount = 0)
            }
            return
        }
        updateState { it.copy(isLoading = true, isLoggedIn = true) }
        coroutineScope {
            val profileDeferred = async { getUserProfileUseCase() }
            val coursesDeferred = async { getSavedCoursesUseCase() }
            val profileResult = profileDeferred.await()
            val coursesResult = coursesDeferred.await()

            updateState {
                it.copy(
                    isLoading = false,
                    name = profileResult.getOrNull()?.name ?: it.name,
                    email = profileResult.getOrNull()?.email ?: it.email,
                    savedCoursesCount = coursesResult.getOrNull()?.totalCount?.toInt() ?: it.savedCoursesCount,
                )
            }
            if (profileResult.isFailure || coursesResult.isFailure) {
                postSideEffect(SettingsSideEffect.ShowLoadError)
            }
        }
    }

    private suspend fun logout() {
        logoutUseCase()
        withContext(NonCancellable) {
            clearDistanceRecordingUseCase()
            clearSessionUseCase()
        }
    }
}
