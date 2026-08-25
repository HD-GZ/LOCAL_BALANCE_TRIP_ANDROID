package live.lb_trip.feature.settings

sealed interface SettingsIntent {
    data object Retry : SettingsIntent
    data object Refresh : SettingsIntent
    data object EditProfileClick : SettingsIntent
    data object LicensesClick : SettingsIntent
    data object TermsClick : SettingsIntent
    data object PrivacyClick : SettingsIntent
    data object ContactClick : SettingsIntent
    data object RetakeDiagnosisClick : SettingsIntent
    data object ProfileUpdated : SettingsIntent
    data object LogoutClick : SettingsIntent
    data object GuestLoginClick : SettingsIntent
    data object DismissAuthPrompt : SettingsIntent
    data object ConfirmAuthPrompt : SettingsIntent
}
