package live.lb_trip.feature.signin

import android.app.Activity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults

private val BottomFadeGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0f to Color.Transparent,
        0.28f to Color.White,
    ),
)

@Composable
internal fun SigninScreen(
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: SigninViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    SigninScreenContent(
        state = state,
        onBack = onBack,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onLoginClick = viewModel::login,
        onNavigateToSignup = onNavigateToSignup,
        modifier = modifier,
    )
}

@Composable
private fun SigninScreenContent(
    state: SigninUiState,
    onBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToSignup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            SigninAppBar(onBackClick = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                SigninHeader()
                Spacer(modifier = Modifier.height(44.dp))
                SigninForm(
                    email = state.email,
                    password = state.password,
                    isPasswordVisible = state.isPasswordVisible,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onTogglePasswordVisibility = onTogglePasswordVisibility,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            SigninBottomAction(
                isLoading = state.isLoading,
                onLoginClick = onLoginClick,
                onForgotPasswordClick = {},
                onSignupClick = onNavigateToSignup,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color(0xFF2F6F4F))
            }
        }
    }
}

@Composable
private fun SigninAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                contentDescription = "뒤로",
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun SigninHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append("로컬")
                withStyle(SpanStyle(color = Color(0xFF2F6F4F))) { append("밸런스") }
                append(" 트립")
            },
            color = Color(0xFF222019),
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.345).sp,
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "로그인",
            color = Color(0xFF2F6F4F),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.54.sp,
        )
        Spacer(modifier = Modifier.height(11.dp))
        Text(
            text = "다시 오신 걸 환영해요",
            color = Color(0xFF222019),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.528).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "내 취향과 예산에 맞춘 로컬 슬로우 트립을 이어서 설계해요.",
            color = Color(0xFF5F5B53),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.4.sp,
        )
    }
}

@Composable
private fun SigninForm(
    email: String,
    password: String,
    isPasswordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        LbInputField(
            value = email,
            onValueChange = onEmailChange,
            label = "이메일",
            placeholder = "local@email.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        LbInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = "비밀번호",
            placeholder = "비밀번호 입력",
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.size(38.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                        contentDescription = if (isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                        tint = Color.Unspecified,
                    )
                }
            },
        )
    }
}

@Composable
private fun LbInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text(
            text = label,
            color = Color(0xFF5F5B53),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = (-0.065).sp,
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            cursorBrush = SolidColor(Color(0xFF2F6F4F)),
            textStyle = TextStyle(
                color = Color(0xFF222019),
                fontSize = 15.5.sp,
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, Color(0xFFD9D5CD), RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(start = 15.dp, end = if (trailingIcon != null) 6.dp else 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFFB8B3AA),
                                fontSize = 15.5.sp,
                            )
                        }
                        innerTextField()
                    }
                    trailingIcon?.invoke()
                }
            },
        )
    }
}

@Composable
private fun SigninBottomAction(
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BottomFadeGradient)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LbButton(
            onClick = onLoginClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = LbButtonDefaults.greenColors(),
        ) {
            Text(
                text = "로그인",
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.155).sp,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "비밀번호 찾기",
                color = Color(0xFF5F5B53),
                fontSize = 13.5.sp,
                modifier = Modifier.clickable(onClick = onForgotPasswordClick),
            )
            Spacer(modifier = Modifier.width(15.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(12.dp)
                    .background(Color(0xFFC3BDB3)),
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "회원가입",
                color = Color(0xFF2F6F4F),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onSignupClick),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SigninScreenPreview() {
    SigninScreenContent(
        state = SigninUiState(),
        onBack = {},
        onEmailChange = {},
        onPasswordChange = {},
        onTogglePasswordVisibility = {},
        onLoginClick = {},
        onNavigateToSignup = {},
    )
}
