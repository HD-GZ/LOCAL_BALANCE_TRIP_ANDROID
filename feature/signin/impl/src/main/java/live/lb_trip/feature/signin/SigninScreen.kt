package live.lb_trip.feature.signin

import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.core.designsystem.component.LbTopBar

@Composable
internal fun SigninScreen(
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToPasswordReset: () -> Unit,
    viewModel: SigninViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current

    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                SigninSideEffect.LoginSucceeded -> onBack()
            }
        }
    }

    SigninScreenContent(
        state = state,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        onNavigateToSignup = onNavigateToSignup,
        onNavigateToPasswordReset = onNavigateToPasswordReset,
        modifier = modifier,
    )
}

@Composable
private fun SigninScreenContent(
    state: SigninUiState,
    onBack: () -> Unit,
    onIntent: (SigninIntent) -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToPasswordReset: () -> Unit,
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
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.signin_back),
                title = stringResource(R.string.signin_brand_prefix) +
                    stringResource(R.string.signin_brand_highlight) +
                    stringResource(R.string.signin_brand_suffix),
                containerColor = Color.Transparent,
                windowInsets = WindowInsets(0, 0, 0, 0),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                    onEmailChange = { onIntent(SigninIntent.EmailChanged(it)) },
                    onPasswordChange = { onIntent(SigninIntent.PasswordChanged(it)) },
                    onTogglePasswordVisibility = { onIntent(SigninIntent.TogglePasswordVisibility) },
                    onLoginClick = { onIntent(SigninIntent.LoginClicked) },
                )
            }

            SigninBottomAction(
                isLoading = state.isLoading,
                onLoginClick = { onIntent(SigninIntent.LoginClicked) },
                onForgotPasswordClick = onNavigateToPasswordReset,
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
            LbLoadingOverlay()
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
            text = stringResource(R.string.signin_welcome),
            color = LbColors.Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.528).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.signin_description),
            color = LbColors.Ink2,
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
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        LbInputField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.signin_email),
            placeholder = stringResource(R.string.signin_email_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
        )
        LbInputField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.signin_password),
            placeholder = stringResource(R.string.signin_password_placeholder),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onLoginClick()
                },
            ),
            textFieldModifier = Modifier.focusRequester(passwordFocusRequester),
            trailingIcon = {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.size(38.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                        contentDescription = stringResource(if (isPasswordVisible) R.string.signin_password_hide else R.string.signin_password_show),
                        tint = Color.Unspecified,
                    )
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
    LbBottomActionBar(
        modifier = modifier,
        windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LbBottomActionButton(
            text = stringResource(R.string.signin_title),
            onClick = onLoginClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.signin_forgot_password),
                color = LbColors.Ink2,
                fontSize = 13.5.sp,
                modifier = Modifier.clickable(onClick = onForgotPasswordClick),
            )
            Spacer(modifier = Modifier.width(15.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(12.dp)
                    .background(LbColors.Line2),
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = stringResource(R.string.signin_signup),
                color = LbColors.Green,
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
        onIntent = {},
        onNavigateToSignup = {},
        onNavigateToPasswordReset = {},
    )
}
