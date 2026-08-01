package live.lb_trip.feature.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.ClearSessionUseCase
import live.lb_trip.domain.usecase.GetSavedCoursesUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase
import live.lb_trip.domain.usecase.LogoutUseCase

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getSavedCoursesUseCase: GetSavedCoursesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
) : BaseViewModel<SettingsUiState, SettingsIntent, SettingsSideEffect>(SettingsUiState()) {

    init {
        viewModelScope.launch { load() }
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Retry -> viewModelScope.launch { load() }
            is SettingsIntent.MenuItemClick -> postSideEffect(SettingsSideEffect.ShowUnavailableMessage(intent.label))
            SettingsIntent.EditProfileClick -> postSideEffect(SettingsSideEffect.NavigateToEditProfile)
            SettingsIntent.RetakeDiagnosisClick -> postSideEffect(SettingsSideEffect.NavigateToDiagnosis)
            SettingsIntent.ProfileUpdated -> viewModelScope.launch {
                load()
                postSideEffect(SettingsSideEffect.ShowProfileUpdated)
            }
            SettingsIntent.LogoutClick -> viewModelScope.launch { logout() }
        }
    }

    private suspend fun load() {
        updateState { it.copy(isLoading = true) }
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
        clearSessionUseCase()
    }
}
