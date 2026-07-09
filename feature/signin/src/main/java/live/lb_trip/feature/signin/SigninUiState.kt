package live.lb_trip.feature.signin

data class SigninUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface SigninEffect {
    data object LoginSucceeded : SigninEffect
}
