package live.lb_trip.feature.settings

import live.lb_trip.domain.model.Gender

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isWithdrawing: Boolean = false,
    val email: String = "",
    val name: String = "",
    val birthYear: String = "",
    val birthMonth: Int = 0,
    val birthDay: String = "",
    val gender: Gender = Gender.NOT_SPECIFIED,
    val password: String = "",
    val passwordConfirm: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
)

sealed interface EditProfileSideEffect {
    data object ShowLoadError : EditProfileSideEffect
    data object SaveSuccess : EditProfileSideEffect
    data class ShowSaveError(val message: String) : EditProfileSideEffect
    data class ShowWithdrawError(val message: String) : EditProfileSideEffect
}
