package live.lb_trip.feature.settings

sealed interface SettingsIntent {
    data object Retry : SettingsIntent
    data class MenuItemClick(val label: String) : SettingsIntent
    data object EditProfileClick : SettingsIntent
    data object RetakeDiagnosisClick : SettingsIntent
    data object ProfileUpdated : SettingsIntent
    data object LogoutClick : SettingsIntent
}
