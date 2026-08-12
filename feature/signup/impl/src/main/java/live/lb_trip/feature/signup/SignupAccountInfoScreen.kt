package live.lb_trip.feature.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.core.designsystem.component.LbStepBar
import live.lb_trip.core.designsystem.component.LbTopBar

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
            backContentDescription = stringResource(R.string.signup_back),
            title = stringResource(R.string.signup_title),
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
            LbStepBar(currentStep = 1, totalSteps = 2)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.signup_account_heading),
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.signup_account_description),
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
                    label = stringResource(R.string.signup_email),
                    placeholder = stringResource(R.string.signup_email_placeholder),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                LbInputField(
                    required = true,
                    value = state.password,
                    onValueChange = { onIntent(SignupIntent.PasswordChanged(it)) },
                    label = stringResource(R.string.signup_password),
                    placeholder = stringResource(R.string.signup_password_placeholder),
                    hintText = stringResource(R.string.signup_password_hint),
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { onIntent(SignupIntent.TogglePasswordVisibility) },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                                contentDescription = stringResource(if (state.isPasswordVisible) R.string.signup_password_hide else R.string.signup_password_show),
                                tint = Color.Unspecified,
                            )
                        }
                    },
                )
                LbInputField(
                    required = true,
                    value = state.passwordConfirm,
                    onValueChange = { onIntent(SignupIntent.PasswordConfirmChanged(it)) },
                    label = stringResource(R.string.signup_password_confirm),
                    placeholder = stringResource(R.string.signup_reenter),
                    visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { onIntent(SignupIntent.ToggleConfirmPasswordVisibility) },
                            modifier = Modifier.size(38.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_eye),
                                contentDescription = stringResource(if (state.isConfirmPasswordVisible) R.string.signup_password_hide else R.string.signup_password_show),
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

        LbBottomActionBar(
            windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LbBottomActionButton(
                text = stringResource(R.string.signup_next),
                onClick = { onIntent(SignupIntent.AccountInfoNextStepClicked) },
                enabled = isAccountInfoValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.signup_existing_account))
                    withStyle(SpanStyle(color = LbColors.Green, fontWeight = FontWeight.SemiBold)) {
                        append(stringResource(R.string.signup_signin))
                    }
                },
                color = LbColors.Ink2,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onIntent(SignupIntent.NavigateToSigninClicked) },
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
