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
            backContentDescription = "뒤로",
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
                text = "비밀번호를 잊으셨나요?",
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "가입하신 이메일로 6자리 인증 코드를 보내드려요.",
                color = LbColors.Ink2,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))
            LbInputField(
                required = true,
                value = state.email,
                onValueChange = { onIntent(PasswordResetIntent.EmailChanged(it)) },
                label = "이메일",
                placeholder = "local@email.com",
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
                    text = "인증 코드 받기",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
            Text(
                text = buildAnnotatedString {
                    append("계정이 기억나셨나요? ")
                    withStyle(SpanStyle(color = LbColors.Green, fontWeight = FontWeight.SemiBold)) {
                        append("로그인")
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
            backContentDescription = "뒤로",
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
                text = "이메일을 확인해 주세요",
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
                        append(" 으로\n6자리 인증 코드를 보냈어요.")
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
                        append("남은 시간 ")
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
                    text = "인증 시간이 만료됐어요. 코드를 재전송해 주세요.",
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
                    text = "코드 재전송",
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
                    text = "다음 단계",
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
            backContentDescription = "뒤로",
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
                text = "새 비밀번호를 설정해요",
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "이전에 사용하지 않은 비밀번호로 설정해 주세요.",
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
                    label = "새 비밀번호",
                    placeholder = "영문·숫자 8자 이상",
                    hintText = "영문·숫자 포함 8자 이상",
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
                                contentDescription = if (state.isNewPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color.Unspecified,
                            )
                        }
                    },
                )
                LbInputField(
                    required = true,
                    value = state.newPasswordConfirm,
                    onValueChange = { onIntent(PasswordResetIntent.NewPasswordConfirmChanged(it)) },
                    label = "새 비밀번호 확인",
                    placeholder = "다시 입력",
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
                                contentDescription = if (state.isNewPasswordConfirmVisible) "비밀번호 숨기기" else "비밀번호 보기",
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
                    text = "비밀번호 변경하기",
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
                    text = "비밀번호를 변경했어요",
                    color = LbColors.Ink,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "새 비밀번호로 다시 로그인해 주세요.\n이전 비밀번호는 더 이상 사용할 수 없어요.",
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
                    text = "로그인하러 가기",
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
