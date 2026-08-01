package live.lb_trip.feature.settings

import live.lb_trip.domain.model.Gender

sealed interface EditProfileIntent {
    data class NameChanged(val value: String) : EditProfileIntent
    data class BirthYearChanged(val value: String) : EditProfileIntent
    data class BirthMonthChanged(val value: Int) : EditProfileIntent
    data class BirthDayChanged(val value: String) : EditProfileIntent
    data class GenderChanged(val value: Gender) : EditProfileIntent
    data class PasswordChanged(val value: String) : EditProfileIntent
    data class PasswordConfirmChanged(val value: String) : EditProfileIntent
    data object TogglePasswordVisibility : EditProfileIntent
    data object ToggleConfirmPasswordVisibility : EditProfileIntent
    data object SaveClicked : EditProfileIntent
    data object WithdrawClicked : EditProfileIntent
}
