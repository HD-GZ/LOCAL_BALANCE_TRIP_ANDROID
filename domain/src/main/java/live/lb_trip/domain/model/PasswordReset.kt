package live.lb_trip.domain.model

data class PasswordResetCodeIssued(
    val verificationCodeExpiresIn: Long,
)

data class PasswordResetToken(
    val resetToken: String,
    val resetTokenExpiresIn: Long,
)
