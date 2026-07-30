package live.lb_trip.feature.settings

data class SettingsUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val email: String = "",
    val savedCoursesCount: Int = 0,
)

sealed interface SettingsSideEffect {
    data object ShowLoadError : SettingsSideEffect
    data class ShowUnavailableMessage(val label: String) : SettingsSideEffect
}
