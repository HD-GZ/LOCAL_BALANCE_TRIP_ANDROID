package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.PasswordResetCodeResponseDto
import live.lb_trip.data.dto.response.PasswordResetConfirmResponseDto
import live.lb_trip.domain.model.PasswordResetCodeIssued
import live.lb_trip.domain.model.PasswordResetToken

fun PasswordResetCodeResponseDto.toDomain(): PasswordResetCodeIssued =
    PasswordResetCodeIssued(verificationCodeExpiresIn = verificationCodeExpiresIn)

fun PasswordResetConfirmResponseDto.toDomain(): PasswordResetToken =
    PasswordResetToken(resetToken = resetToken, resetTokenExpiresIn = resetTokenExpiresIn)
