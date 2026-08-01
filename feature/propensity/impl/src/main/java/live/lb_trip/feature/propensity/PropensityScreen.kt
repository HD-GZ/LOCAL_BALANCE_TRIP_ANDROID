package live.lb_trip.feature.propensity

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.core.designsystem.component.LbScaleSelector
import live.lb_trip.core.designsystem.component.LbStepIndicator

private val TextPrimary = LbColors.Ink
private val TextSecondary = LbColors.Ink2
private val TextMuted = LbColors.Ink4
private val Brand = LbColors.Green
private val Border = LbColors.LineSoft
private val OutlineBorder = LbColors.Line2
private val BodyBackground = LbColors.ScreenBg

@Composable
internal fun PropensityPreferenceScreen(
    state: PropensityUiState,
    onBack: () -> Unit,
    onIntent: (PropensityIntent) -> Unit,
    onNextStep: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PropensityStepShell(
        stepNumber = 1,
        title = stringResource(R.string.propensity_title_preference),
        headerTitle = stringResource(R.string.propensity_header_preference_title),
        headerSubtitle = stringResource(R.string.propensity_header_preference_subtitle),
        state = state,
        onBack = onBack,
        modifier = modifier,
    ) {
        PreferenceStepContent(
            state = state,
            onIntent = onIntent,
            onNextStep = onNextStep,
        )
    }
}

@Composable
internal fun PropensityValueConsumptionScreen(
    state: PropensityUiState,
    onBack: () -> Unit,
    onIntent: (PropensityIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PropensityStepShell(
        stepNumber = 2,
        title = stringResource(R.string.propensity_title_value_consumption),
        headerTitle = stringResource(R.string.propensity_header_value_consumption_title),
        headerSubtitle = stringResource(R.string.propensity_header_value_consumption_subtitle),
        state = state,
        onBack = onBack,
        modifier = modifier,
    ) {
        ValueConsumptionStepContent(
            state = state,
            onIntent = onIntent,
            onBack = onBack,
        )
    }
}

@Composable
internal fun PropensityResultScreen(
    state: PropensityUiState,
    onBack: () -> Unit,
    onIntent: (PropensityIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    PropensityStepShell(
        stepNumber = 3,
        title = stringResource(R.string.propensity_title_result),
        headerTitle = stringResource(R.string.propensity_header_result_title),
        headerSubtitle = stringResource(R.string.propensity_header_result_subtitle),
        state = state,
        onBack = onBack,
        modifier = modifier,
    ) {
        ResultStepContent(
            state = state,
            onIntent = onIntent,
        )
    }
}

@Composable
private fun PropensityStepShell(
    stepNumber: Int,
    title: String,
    headerTitle: String,
    headerSubtitle: String,
    state: PropensityUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            LbTopBar(
                title = title,
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.propensity_back_content_description),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BodyBackground)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 22.dp, vertical = 20.dp),
            ) {
                LbStepIndicator(
                    currentStep = stepNumber,
                    totalSteps = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                PropensityHeader(title = headerTitle, subtitle = headerSubtitle)

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(width = 1.dp, color = Border, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    content()
                }
            }
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
private fun PropensityHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun PreferenceStepContent(
    state: PropensityUiState,
    onIntent: (PropensityIntent) -> Unit,
    onNextStep: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AxisRow(
            title = stringResource(R.string.propensity_axis_locality_title),
            leftLabel = stringResource(R.string.propensity_axis_locality_left),
            leftSub = stringResource(R.string.propensity_axis_locality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_locality_right),
            rightSub = stringResource(R.string.propensity_axis_locality_right_sub),
            value = state.locality,
            onValueChange = { onIntent(PropensityIntent.LocalityChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_frugality_title),
            leftLabel = stringResource(R.string.propensity_axis_frugality_left),
            leftSub = stringResource(R.string.propensity_axis_frugality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_frugality_right),
            rightSub = stringResource(R.string.propensity_axis_frugality_right_sub),
            value = state.frugality,
            onValueChange = { onIntent(PropensityIntent.FrugalityChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_experientiality_title),
            leftLabel = stringResource(R.string.propensity_axis_experientiality_left),
            leftSub = stringResource(R.string.propensity_axis_experientiality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_experientiality_right),
            rightSub = stringResource(R.string.propensity_axis_experientiality_right_sub),
            value = state.experientiality,
            onValueChange = { onIntent(PropensityIntent.ExperientialityChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_vitality_title),
            leftLabel = stringResource(R.string.propensity_axis_vitality_left),
            leftSub = stringResource(R.string.propensity_axis_vitality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_vitality_right),
            rightSub = stringResource(R.string.propensity_axis_vitality_right_sub),
            value = state.vitality,
            onValueChange = { onIntent(PropensityIntent.VitalityChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_sociality_title),
            leftLabel = stringResource(R.string.propensity_axis_sociality_left),
            leftSub = stringResource(R.string.propensity_axis_sociality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_sociality_right),
            rightSub = stringResource(R.string.propensity_axis_sociality_right_sub),
            value = state.sociality,
            onValueChange = { onIntent(PropensityIntent.SocialityChanged(it)) },
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        LbButton(
            onClick = onNextStep,
            colors = LbButtonDefaults.greenColors(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text(
                text = stringResource(R.string.propensity_cta_set_value_consumption),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ValueConsumptionStepContent(
    state: PropensityUiState,
    onIntent: (PropensityIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val save = stringResource(R.string.propensity_save)
    val saveEn = stringResource(R.string.propensity_save_en)
    val spend = stringResource(R.string.propensity_spend)
    val spendEn = stringResource(R.string.propensity_spend_en)

    Column(modifier = modifier.fillMaxWidth()) {
        AxisRow(
            title = stringResource(R.string.propensity_item_accommodation),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.accommodation,
            onValueChange = { onIntent(PropensityIntent.AccommodationChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_food),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.food,
            onValueChange = { onIntent(PropensityIntent.FoodChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_experience),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.experience,
            onValueChange = { onIntent(PropensityIntent.ExperienceChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_transportation),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.transportation,
            onValueChange = { onIntent(PropensityIntent.TransportationChanged(it)) },
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_cafe_exhibition),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.cafeExhibition,
            onValueChange = { onIntent(PropensityIntent.CafeExhibitionChanged(it)) },
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Border, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LbButton(
                onClick = onBack,
                colors = LbButtonDefaults.whiteColors(),
                border = BorderStroke(width = 1.dp, color = OutlineBorder),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.propensity_cta_previous_step),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            LbButton(
                onClick = { onIntent(PropensityIntent.SubmitAndViewResult) },
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.propensity_cta_view_result),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultStepContent(
    state: PropensityUiState,
    onIntent: (PropensityIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = state.resultType.orEmpty(),
            color = Brand,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = state.resultDescription.orEmpty(),
            color = TextSecondary,
            fontSize = 12.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LbButton(
                onClick = { onIntent(PropensityIntent.RestartDiagnosis) },
                colors = LbButtonDefaults.whiteColors(),
                border = BorderStroke(width = 1.dp, color = OutlineBorder),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.propensity_cta_restart),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            LbButton(
                onClick = { onIntent(PropensityIntent.CourseRecommendationClicked) },
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Text(
                    text = stringResource(R.string.propensity_cta_course_recommendation),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun AxisRow(
    title: String,
    leftLabel: String,
    leftSub: String,
    rightLabel: String,
    rightSub: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val leftEmphasis = value < 3
    val rightEmphasis = value > 3

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 14.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = leftLabel,
                    color = if (leftEmphasis) Brand else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = leftSub,
                    color = if (leftEmphasis) Brand else TextMuted,
                    fontSize = 10.5.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = rightLabel,
                    color = if (rightEmphasis) Brand else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                )
                Text(
                    text = rightSub,
                    color = if (rightEmphasis) Brand else TextMuted,
                    fontSize = 10.5.sp,
                    textAlign = TextAlign.End,
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LbScaleSelector(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
private fun PropensityPreferencePreview() {
    PropensityPreferenceScreen(
        state = PropensityUiState(),
        onBack = {},
        onIntent = {},
        onNextStep = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PropensityValueConsumptionPreview() {
    PropensityValueConsumptionScreen(
        state = PropensityUiState(),
        onBack = {},
        onIntent = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PropensityResultPreview() {
    PropensityResultScreen(
        state = PropensityUiState(
            resultType = "실속형 로컬 체험 여행자",
            resultDescription = "럭셔리보다 실속을, 유명 명소보다 골목 상권을, 눈으로 보는 관람보다 직접 해보는 체험을 즐기는 홀로 떠나는 1인 여행자예요.",
        ),
        onBack = {},
        onIntent = {},
    )
}
