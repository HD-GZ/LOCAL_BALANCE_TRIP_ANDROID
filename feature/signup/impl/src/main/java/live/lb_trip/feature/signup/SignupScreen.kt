package live.lb_trip.feature.signup

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import live.lb_trip.domain.model.Gender

@Composable
internal fun SignupAccountInfoScreen(
    state: SignupUiState,
    onBack: () -> Unit,
    onIntent: (SignupIntent) -> Unit,
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
            title = "회원가입",
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
            SignupStepBar(currentStep = 1, totalSteps = 2)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "계정 정보를 입력해요",
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "로그인에 사용할 이메일과 비밀번호예요.",
                color = LbColors.Ink2,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LbInputField(
                    required = true,
                    value = state.email,
                    onValueChange = { onIntent(SignupIntent.EmailChanged(it)) },
                    label = "이메일",
                    placeholder = "local@email.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                LbInputField(
                    required = true,
                    value = state.password,
                    onValueChange = { onIntent(SignupIntent.PasswordChanged(it)) },
                    label = "비밀번호",
                    placeholder = "영문·숫자 8자 이상",
                    hintText = "영문·숫자 포함 8자 이상",
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { onIntent(SignupIntent.TogglePasswordVisibility) },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                                contentDescription = if (state.isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color.Unspecified,
                            )
                        }
                    },
                )
                LbInputField(
                    required = true,
                    value = state.passwordConfirm,
                    onValueChange = { onIntent(SignupIntent.PasswordConfirmChanged(it)) },
                    label = "비밀번호 확인",
                    placeholder = "다시 입력",
                    visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { onIntent(SignupIntent.ToggleConfirmPasswordVisibility) },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                                contentDescription = if (state.isConfirmPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color.Unspecified,
                            )
                        }
                    },
                )
            }
        }

        val isAccountInfoValid = state.email.isNotEmpty() &&
            state.password.isNotEmpty() &&
            state.passwordConfirm.isNotEmpty() &&
            state.password == state.passwordConfirm

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
                onClick = { onIntent(SignupIntent.AccountInfoNextStepClicked) },
                enabled = isAccountInfoValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
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
            Text(
                text = buildAnnotatedString {
                    append("이미 회원이신가요? ")
                    withStyle(SpanStyle(color = LbColors.Green, fontWeight = FontWeight.SemiBold)) {
                        append("로그인")
                    }
                },
                color = LbColors.Ink2,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onIntent(SignupIntent.NavigateToSigninClicked) },
            )
        }
    }
}

@Composable
internal fun SignupPersonalInfoScreen(
    state: SignupUiState,
    onBack: () -> Unit,
    onIntent: (SignupIntent) -> Unit,
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
            title = "회원가입",
            containerColor = Color.Transparent,
            windowInsets = WindowInsets(0, 0, 0, 0),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            SignupStepBar(currentStep = 2, totalSteps = 2)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "기본 정보를 입력해요",
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "생년월일은 청년 혜택 판별에 쓰여요.",
                color = LbColors.Ink2,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            LbInputField(
                required = true,
                value = state.name,
                onValueChange = { onIntent(SignupIntent.NameChanged(it)) },
                label = "이름",
                placeholder = "홍길동",
            )
            Spacer(modifier = Modifier.height(16.dp))

            BirthField(
                year = state.birthYear,
                month = state.birthMonth,
                day = state.birthDay,
                onYearChange = { onIntent(SignupIntent.BirthYearChanged(it)) },
                onMonthChange = { onIntent(SignupIntent.BirthMonthChanged(it)) },
                onDayChange = { onIntent(SignupIntent.BirthDayChanged(it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = "성별",
                    color = LbColors.Ink2,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.065).sp,
                )
                GenderSegmented(
                    selected = state.gender,
                    onSelect = { onIntent(SignupIntent.GenderChanged(it)) },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            AgreeBlock(
                termsAgreed = state.termsAgreed,
                privacyAgreed = state.privacyAgreed,
                marketingAgreed = state.marketingAgreed,
                onToggleTos = { onIntent(SignupIntent.ToggleTos) },
                onTogglePrivacy = { onIntent(SignupIntent.TogglePrivacy) },
                onToggleMarketing = { onIntent(SignupIntent.ToggleMarketing) },
                onToggleAll = { onIntent(SignupIntent.ToggleAllTerms) },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        val isPersonalInfoValid = state.name.isNotEmpty() &&
            state.birthYear.length == 4 &&
            state.birthMonth in 1..12 &&
            (state.birthDay.toIntOrNull() ?: 0) in 1..31 &&
            state.termsAgreed &&
            state.privacyAgreed

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbBrush.BottomFadeGradient)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 14.dp),
        ) {
            LbButton(
                onClick = { onIntent(SignupIntent.PersonalInfoNextStepClicked) },
                enabled = isPersonalInfoValid && !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
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
private fun BirthField(
    year: String,
    month: Int,
    day: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (Int) -> Unit,
    onDayChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var monthMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append("생년월일")
                withStyle(SpanStyle(color = LbColors.RequiredMark, fontWeight = FontWeight.SemiBold)) {
                    append(" *")
                }
            },
            color = LbColors.Ink2,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.065).sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicTextField(
                value = year,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) onYearChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(LbColors.Green),
                textStyle = TextStyle(color = LbColors.Ink, fontSize = 15.5.sp),
                modifier = Modifier.weight(2.2f),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (year.isEmpty()) {
                            Text(text = "YYYY", color = LbColors.Ink4, fontSize = 15.5.sp)
                        }
                        innerTextField()
                    }
                },
            )

            Box(modifier = Modifier.weight(1.5f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { monthMenuExpanded = true }
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = if (month == 0) "월" else "${month}월",
                        color = if (month == 0) LbColors.Ink4 else LbColors.Ink,
                        fontSize = 15.5.sp,
                    )
                }
                DropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false },
                ) {
                    for (m in 1..12) {
                        DropdownMenuItem(
                            text = { Text("${m}월") },
                            onClick = {
                                onMonthChange(m)
                                monthMenuExpanded = false
                            },
                        )
                    }
                }
            }

            BasicTextField(
                value = day,
                onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) onDayChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(LbColors.Green),
                textStyle = TextStyle(color = LbColors.Ink, fontSize = 15.5.sp),
                modifier = Modifier.weight(1.2f),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (day.isEmpty()) {
                            Text(text = "일", color = LbColors.Ink4, fontSize = 15.5.sp)
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun GenderSegmented(
    selected: Gender,
    onSelect: (Gender) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        Gender.MALE to "남성",
        Gender.FEMALE to "여성",
        Gender.NOT_SPECIFIED to "선택 안 함",
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
    ) {
        options.forEachIndexed { index, (gender, label) ->
            val isSelected = selected == gender
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (isSelected) LbColors.Green else Color.White)
                    .clickable { onSelect(gender) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else LbColors.Ink2,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    letterSpacing = (-0.14).sp,
                )
            }
            if (index < options.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(LbColors.Line),
                )
            }
        }
    }
}

@Composable
private fun AgreeBlock(
    termsAgreed: Boolean,
    privacyAgreed: Boolean,
    marketingAgreed: Boolean,
    onToggleTos: () -> Unit,
    onTogglePrivacy: () -> Unit,
    onToggleMarketing: () -> Unit,
    onToggleAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allAgreed = termsAgreed && privacyAgreed && marketingAgreed

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleAll)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AgreeCheckbox(checked = allAgreed)
            Text(
                text = "약관에 모두 동의합니다",
                color = LbColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LbColors.LineSoft),
        )

        AgreeRow(
            checked = termsAgreed,
            label = "서비스 이용약관",
            required = true,
            onClick = onToggleTos,
        )
        AgreeRow(
            checked = privacyAgreed,
            label = "개인정보 처리방침",
            required = true,
            onClick = onTogglePrivacy,
        )
        AgreeRow(
            checked = marketingAgreed,
            label = "마케팅 수신 동의",
            required = false,
            onClick = onToggleMarketing,
        )
    }
}

@Composable
private fun AgreeRow(
    checked: Boolean,
    label: String,
    required: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AgreeCheckbox(checked = checked)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (required) "[필수]" else "[선택]",
                color = if (required) LbColors.Green else LbColors.Ink3,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = label,
                color = LbColors.Ink2,
                fontSize = 13.5.sp,
            )
        }
    }
}

@Composable
private fun AgreeCheckbox(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (checked) LbColors.Green else Color.Transparent)
            .border(1.dp, if (checked) LbColors.Green else LbColors.Line2, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
internal fun SignupEmailVerifyScreen(
    state: SignupUiState,
    onBack: () -> Unit,
    onIntent: (SignupIntent) -> Unit,
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
            title = "이메일 인증",
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
            Text(
                text = "이메일을 확인해 주세요",
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(61.dp))
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
            Spacer(modifier = Modifier.height(61.dp))
            OtpInputField(
                code = state.code,
                onCodeChange = { onIntent(SignupIntent.CodeChanged(it)) },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = buildAnnotatedString {
                    append("남은 시간 ")
                    withStyle(SpanStyle(color = Color(0xFF3C875F), fontWeight = FontWeight.SemiBold)) {
                        append("%02d:%02d".format(minutes, seconds))
                    }
                },
                color = LbColors.Ink3,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
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
                onClick = { onIntent(SignupIntent.ResendCodeClicked) },
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
                onClick = { onIntent(SignupIntent.ConfirmCodeClicked) },
                enabled = isCodeComplete && !state.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = LbButtonDefaults.greenColors(),
            ) {
                Text(
                    text = "인증 완료",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
        }
    }
}

@Composable
internal fun SignupCompleteScreen(
    state: SignupUiState,
    onIntent: (SignupIntent) -> Unit,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = LbColors.Green)) {
                            append(state.name.ifEmpty { "여행자" })
                        }
                        append("님, 환영해요!")
                    },
                    color = LbColors.Ink,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.5).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "이메일 인증이 완료되어 회원가입이 끝났어요.\n이제 취향에 맞는 로컬 슬로우 트립을 만나보세요.",
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
                onClick = { onIntent(SignupIntent.NavigateToSigninClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = LbButtonDefaults.greenColors(),
            ) {
                Text(
                    text = "로그인하고 여행 시작하기",
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.155).sp,
                )
            }
        }
    }
}

@Composable
private fun SignupStepBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(totalSteps) { index ->
            val step = index + 1
            val isActive = step <= currentStep
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (isActive) LbColors.Green else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isActive) LbColors.Green else LbColors.Line2,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = step.toString(),
                    color = if (isActive) Color.White else LbColors.Ink3,
                    fontSize = 12.sp,
                )
            }
            if (index < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(LbColors.LineSoft),
                )
            }
        }
    }
}

@Composable
private fun OtpInputField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = code,
        onValueChange = { new ->
            if (new.length <= 6 && new.all { it.isDigit() }) onCodeChange(new)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = modifier,
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 0..2) {
                    OtpBox(char = code.getOrNull(i), isCurrent = code.length == i)
                    if (i < 2) Spacer(modifier = Modifier.width(9.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                for (i in 3..5) {
                    OtpBox(char = code.getOrNull(i), isCurrent = code.length == i)
                    if (i < 5) Spacer(modifier = Modifier.width(9.dp))
                }
            }
        },
    )
}

@Composable
private fun OtpBox(
    char: Char?,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 46.dp, height = 58.dp)
            .border(
                width = 1.dp,
                color = if (isCurrent) LbColors.Green else LbColors.Line,
                shape = RoundedCornerShape(13.dp),
            )
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        if (char != null) {
            Text(
                text = char.toString(),
                color = LbColors.Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignupAccountInfoPreview() {
    SignupAccountInfoScreen(
        state = SignupUiState(),
        onBack = {},
        onIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SignupPersonalInfoPreview() {
    SignupPersonalInfoScreen(
        state = SignupUiState(),
        onBack = {},
        onIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SignupEmailVerifyPreview() {
    SignupEmailVerifyScreen(
        state = SignupUiState(email = "local@email.com"),
        onBack = {},
        onIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SignupCompletePreview() {
    SignupCompleteScreen(
        state = SignupUiState(name = "여행자"),
        onIntent = {},
    )
}
