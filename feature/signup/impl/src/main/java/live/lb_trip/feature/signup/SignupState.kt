package live.lb_trip.feature.signup

import com.slack.circuit.runtime.CircuitUiState
import live.lb_trip.domain.model.Gender

enum class SignupStep { AccountInfo, PersonalInfo, EmailVerify, Complete }

data class SignupState(
    val step: SignupStep = SignupStep.AccountInfo,
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val name: String = "",
    val birthYear: String = "",
    val birthMonth: Int = 0,
    val birthDay: String = "",
    val gender: Gender = Gender.NOT_SPECIFIED,
    val termsAgreed: Boolean = false,
    val privacyAgreed: Boolean = false,
    val marketingAgreed: Boolean = false,
    val code: String = "",
    val remainingSeconds: Int = 300,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val eventSink: (SignupEvent) -> Unit = {},
) : CircuitUiState
