package live.lb_trip.feature.settings

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val name: String = "",
    val email: String = "",
    val savedCoursesCount: Int = 0,
    val showAuthPrompt: Boolean = false,
)

sealed interface SettingsSideEffect {
    data object ShowLoadError : SettingsSideEffect
    data object NavigateToDiagnosis : SettingsSideEffect
    data object NavigateToEditProfile : SettingsSideEffect
    data object NavigateToLicenses : SettingsSideEffect
    data object NavigateToTerms : SettingsSideEffect
    data object NavigateToPrivacy : SettingsSideEffect
    data object NavigateToSignin : SettingsSideEffect
    data object OpenContactEmail : SettingsSideEffect
    data object ShowProfileUpdated : SettingsSideEffect
}
