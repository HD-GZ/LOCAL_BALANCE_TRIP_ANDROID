package live.lb_trip.feature.signin

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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            LbStepBar(currentStep = 1, totalSteps = 3)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.email.isNotEmpty() && !state.isLoading) onIntent(PasswordResetIntent.SendCodeClicked)
                    },
                ),
            )
        }

        LbBottomActionBar(
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime),
        ) {
            LbBottomActionButton(
                text = stringResource(R.string.password_reset_get_code),
                onClick = { onIntent(PasswordResetIntent.SendCodeClicked) },
                enabled = state.email.isNotEmpty() && !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
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

@Preview(showBackground = true)
@Composable
private fun PasswordResetEmailPreview() {
    PasswordResetEmailScreen(state = PasswordResetUiState(), onBack = {}, onIntent = {})
}
