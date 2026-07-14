package live.lb_trip.feature.signin

sealed interface SigninIntent {
    data class EmailChanged(val value: String) : SigninIntent
    data class PasswordChanged(val value: String) : SigninIntent
    data object TogglePasswordVisibility : SigninIntent
    data object LoginClicked : SigninIntent
}
