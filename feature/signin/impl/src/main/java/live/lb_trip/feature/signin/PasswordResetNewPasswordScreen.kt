package live.lb_trip.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.core.designsystem.component.LbTopBar

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

        LbBottomActionBar {
            LbBottomActionButton(
                text = stringResource(R.string.password_reset_change_password),
                onClick = { onIntent(PasswordResetIntent.ResetPasswordClicked) },
                enabled = isNewPasswordValid && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordResetNewPasswordPreview() {
    PasswordResetNewPasswordScreen(state = PasswordResetUiState(), onBack = {}, onIntent = {})
}
