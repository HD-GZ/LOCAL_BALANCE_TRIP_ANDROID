package live.lb_trip.feature.signin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBrush
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.core.designsystem.component.LbTopBar

@Composable
internal fun PasswordResetEmailScreen(
    state: PasswordResetUiState,
    onBack: () -> Unit,
    onIntent: (PasswordResetIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        LbTopBar(
            onBackClick = onBack,
            backContentDescription = stringResource(R.string.signin_back),
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            PasswordResetStepBar(currentStep = 1)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.password_reset_forgot_title),
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.password_reset_email_description),
                color = LbColors.Ink2,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))
            LbInputField(
                required = true,
                value = state.email,
                onValueChange = { onIntent(PasswordResetIntent.EmailChanged(it)) },
                label = stringResource(R.string.signin_email),
                placeholder = stringResource(R.string.signin_email_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbBrush.BottomFadeGradient)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LbButton(
                onClick = { onIntent(PasswordResetIntent.SendCodeClicked) },
                enabled = state.email.isNotEmpty() && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = LbButtonDefaults.greenColors(),
            ) {
                Text(
                    text = stringResource(R.string.password_reset_get_code),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.password_reset_remembered))
                    withStyle(SpanStyle(color = LbColors.Green, fontWeight = FontWeight.SemiBold)) {
                        append(stringResource(R.string.signin_title))
                    }
                },
                color = LbColors.Ink2,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onIntent(PasswordResetIntent.NavigateToSigninClicked) },
            )
        }
    }
}

@Composable
internal fun PasswordResetVerifyScreen(
    state: PasswordResetUiState,
    onBack: () -> Unit,
    onIntent: (PasswordResetIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val minutes = state.remainingSeconds / 60
    val seconds = state.remainingSeconds % 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        LbTopBar(
            onBackClick = onBack,
            backContentDescription = stringResource(R.string.signin_back),
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            PasswordResetStepBar(currentStep = 2)
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.password_reset_check_email),
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(state.email) }
                    withStyle(SpanStyle(color = LbColors.Ink2, fontWeight = FontWeight.Normal)) {
                        append(stringResource(R.string.password_reset_code_sent_suffix))
                    }
                },
                color = LbColors.Ink,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(40.dp))
            PasswordResetOtpField(
                code = state.code,
                onCodeChange = { onIntent(PasswordResetIntent.CodeChanged(it)) },
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (state.remainingSeconds > 0) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.password_reset_remaining_time))
                        withStyle(SpanStyle(color = LbColors.Green, fontWeight = FontWeight.SemiBold)) {
                            append("%02d:%02d".format(minutes, seconds))
                        }
                    },
                    color = LbColors.Ink3,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text(
                    text = stringResource(R.string.password_reset_expired),
                    color = LbColors.RequiredMark,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }

        val isCodeComplete = state.code.length == 6
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbBrush.BottomFadeGradient)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LbButton(
                onClick = { onIntent(PasswordResetIntent.ResendCodeClicked) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = LbButtonDefaults.whiteColors(),
                border = BorderStroke(1.dp, LbColors.Line2),
            ) {
                Text(
                    text = stringResource(R.string.password_reset_resend_code),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
            LbButton(
                onClick = { onIntent(PasswordResetIntent.ConfirmCodeClicked) },
                enabled = isCodeComplete && !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = LbButtonDefaults.greenColors(),
            ) {
                Text(
                    text = stringResource(R.string.password_reset_next),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
        }
    }
}

@Composable
internal fun PasswordResetNewPasswordScreen(
    state: PasswordResetUiState,
    onBack: () -> Unit,
    onIntent: (PasswordResetIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        LbTopBar(
            onBackClick = onBack,
            backContentDescription = stringResource(R.string.signin_back),
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            PasswordResetStepBar(currentStep = 3)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.password_reset_new_password_title),
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.password_reset_new_password_description),
                color = LbColors.Ink2,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LbInputField(
                    required = true,
                    value = state.newPassword,
                    onValueChange = { onIntent(PasswordResetIntent.NewPasswordChanged(it)) },
                    label = stringResource(R.string.password_reset_new_password),
                    placeholder = stringResource(R.string.password_reset_password_placeholder),
                    hintText = stringResource(R.string.password_reset_password_hint),
                    visualTransformation = if (state.isNewPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { onIntent(PasswordResetIntent.ToggleNewPasswordVisibility) },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                                contentDescription = stringResource(if (state.isNewPasswordVisible) R.string.signin_password_hide else R.string.signin_password_show),
                                tint = Color.Unspecified,
                            )
                        }
                    },
                )
                LbInputField(
                    required = true,
                    value = state.newPasswordConfirm,
                    onValueChange = { onIntent(PasswordResetIntent.NewPasswordConfirmChanged(it)) },
                    label = stringResource(R.string.password_reset_new_password_confirm),
                    placeholder = stringResource(R.string.password_reset_reenter),
                    visualTransformation = if (state.isNewPasswordConfirmVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { onIntent(PasswordResetIntent.ToggleNewPasswordConfirmVisibility) },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                                contentDescription = stringResource(if (state.isNewPasswordConfirmVisible) R.string.signin_password_hide else R.string.signin_password_show),
                                tint = Color.Unspecified,
                            )
                        }
                    },
                )
            }
        }

        val isNewPasswordValid = state.newPassword.isNotEmpty() &&
            state.newPasswordConfirm.isNotEmpty() &&
            state.newPassword == state.newPasswordConfirm

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbBrush.BottomFadeGradient)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 14.dp),
        ) {
            LbButton(
                onClick = { onIntent(PasswordResetIntent.ResetPasswordClicked) },
                enabled = isNewPasswordValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = LbButtonDefaults.greenColors(),
            ) {
                Text(
                    text = stringResource(R.string.password_reset_change_password),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
        }
    }
}

@Composable
internal fun PasswordResetCompleteScreen(
    onIntent: (PasswordResetIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(LbColors.GreenTint)
                        .border(1.dp, LbColors.Green, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp),
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.password_reset_complete_title),
                    color = LbColors.Ink,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.password_reset_complete_description),
                    color = LbColors.Ink2,
                    fontSize = 14.sp,
                    lineHeight = 23.1.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbBrush.BottomFadeGradient)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 14.dp),
        ) {
            LbButton(
                onClick = { onIntent(PasswordResetIntent.NavigateToSigninClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = LbButtonDefaults.greenColors(),
            ) {
                Text(
                    text = stringResource(R.string.password_reset_go_signin),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordResetEmailPreview() {
    PasswordResetEmailScreen(state = PasswordResetUiState(), onBack = {}, onIntent = {})
}

@Preview(showBackground = true)
@Composable
private fun PasswordResetVerifyPreview() {
    PasswordResetVerifyScreen(
        state = PasswordResetUiState(email = "local@email.com", remainingSeconds = 300),
        onBack = {},
        onIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordResetNewPasswordPreview() {
    PasswordResetNewPasswordScreen(state = PasswordResetUiState(), onBack = {}, onIntent = {})
}

@Preview(showBackground = true)
@Composable
private fun PasswordResetCompletePreview() {
    PasswordResetCompleteScreen(onIntent = {})
}
