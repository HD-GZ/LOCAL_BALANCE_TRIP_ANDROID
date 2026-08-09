package live.lb_trip.feature.signup

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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbBirthField
import live.lb_trip.core.designsystem.component.LbBrush
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.feature.signup.components.AgreeBlock
import live.lb_trip.feature.signup.components.GenderSegmented
import live.lb_trip.feature.signup.components.SignupStepBar

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
            SignupStepBar(currentStep = 2, totalSteps = 2)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = stringResource(R.string.signup_personal_heading),
                color = LbColors.Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.528).sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.signup_personal_description),
                color = LbColors.Ink2,
                fontSize = 14.sp,
                lineHeight = 22.4.sp,
            )
            Spacer(modifier = Modifier.height(22.dp))

            LbInputField(
                required = true,
                value = state.name,
                onValueChange = { onIntent(SignupIntent.NameChanged(it)) },
                label = stringResource(R.string.signup_name),
                placeholder = stringResource(R.string.signup_name_placeholder),
            )
            Spacer(modifier = Modifier.height(16.dp))

            LbBirthField(
                year = state.birthYear,
                month = state.birthMonth,
                day = state.birthDay,
                onYearChange = { onIntent(SignupIntent.BirthYearChanged(it)) },
                onMonthChange = { onIntent(SignupIntent.BirthMonthChanged(it)) },
                onDayChange = { onIntent(SignupIntent.BirthDayChanged(it)) },
                label = stringResource(R.string.signup_birth_date),
                required = true,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = stringResource(R.string.signup_gender),
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
                    text = stringResource(R.string.signup_next),
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
private fun SignupPersonalInfoPreview() {
    SignupPersonalInfoScreen(
        state = SignupUiState(),
        onBack = {},
        onIntent = {},
    )
}
