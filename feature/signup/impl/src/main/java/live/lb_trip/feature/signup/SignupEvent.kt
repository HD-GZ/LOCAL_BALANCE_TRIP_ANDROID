package live.lb_trip.feature.signup

import com.slack.circuit.runtime.CircuitUiEvent
import live.lb_trip.domain.model.Gender

sealed interface SignupEvent : CircuitUiEvent {
    data object NavigateBack : SignupEvent
    data class UpdateEmail(val email: String) : SignupEvent
    data class UpdatePassword(val password: String) : SignupEvent
    data class UpdatePasswordConfirm(val passwordConfirm: String) : SignupEvent
    data object TogglePasswordVisibility : SignupEvent
    data object ToggleConfirmPasswordVisibility : SignupEvent
    data object NextStep : SignupEvent
    data class UpdateName(val name: String) : SignupEvent
    data class UpdateBirthYear(val year: String) : SignupEvent
    data class UpdateBirthMonth(val month: Int) : SignupEvent
    data class UpdateBirthDay(val day: String) : SignupEvent
    data class UpdateGender(val gender: Gender) : SignupEvent
    data object ToggleTos : SignupEvent
    data object TogglePrivacy : SignupEvent
    data object ToggleMarketing : SignupEvent
    data object ToggleAllTerms : SignupEvent
    data class UpdateCode(val code: String) : SignupEvent
    data object ResendCode : SignupEvent
    data object ConfirmCode : SignupEvent
    data object NavigateToSignin : SignupEvent
}
