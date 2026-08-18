package live.lb_trip.feature.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbBottomActionButtonRow
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbOtpField
import live.lb_trip.core.designsystem.component.LbTopBar

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
            backContentDescription = stringResource(R.string.signup_back),
            title = stringResource(R.string.signup_verification_title),
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.signup_check_email),
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(61.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = LbColors.Ink2, fontWeight = FontWeight.Normal)) {
                        append(stringResource(R.string.signup_code_sent_prefix))
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(state.email) }
                    withStyle(SpanStyle(color = LbColors.Ink2, fontWeight = FontWeight.Normal)) {
                        append(stringResource(R.string.signup_code_sent_suffix))
                    }
                },
                color = LbColors.Ink,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(61.dp))
            LbOtpField(
                code = state.code,
                onCodeChange = { onIntent(SignupIntent.CodeChanged(it)) },
                onDone = {
                    if (state.code.length == 6 && !state.isLoading) onIntent(SignupIntent.ConfirmCodeClicked)
                },
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.signup_remaining_time))
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
        LbBottomActionBar(windowInsets = WindowInsets.navigationBars.union(WindowInsets.ime)) {
            LbBottomActionButtonRow {
                LbBottomActionButton(
                    text = stringResource(R.string.signup_resend_code),
                    onClick = { onIntent(SignupIntent.ResendCodeClicked) },
                    enabled = !state.isLoading,
                    colors = LbButtonDefaults.whiteColors(),
                    border = BorderStroke(1.dp, LbColors.Line2),
                )
                LbBottomActionButton(
                    text = stringResource(R.string.signup_confirm_code),
                    onClick = { onIntent(SignupIntent.ConfirmCodeClicked) },
                    enabled = isCodeComplete && !state.isLoading,
                )
            }
        }
    }
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
