package live.lb_trip.feature.propensity

import android.app.Activity
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbScaleSelector
import live.lb_trip.core.designsystem.component.LbStepIndicator

private val TextPrimary = Color(0xFF222019)
private val TextSecondary = Color(0xFF5F5B53)
private val TextMuted = Color(0xFFB8B3AA)
private val Brand = Color(0xFF2F6F4F)
private val Border = Color(0xFFEBE7DF)
private val OutlineBorder = Color(0xFFC3BDB3)
private val BodyBackground = Color(0xFFF3F1EC)

@Composable
internal fun PropensityScreen(
    onBack: () -> Unit,
    onNavigateToRecommendation: () -> Unit,
    viewModel: PropensityViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                PropensitySideEffect.NavigateBack -> onBack()
                PropensitySideEffect.NavigateToRecommendation -> onNavigateToRecommendation()
            }
        }
    }

    PropensityScreenContent(
        state = state,
        onBack = viewModel::navigateBack,
        onLocalityChange = viewModel::updateLocality,
        onFrugalityChange = viewModel::updateFrugality,
        onExperientialityChange = viewModel::updateExperientiality,
        onVitalityChange = viewModel::updateVitality,
        onSocialityChange = viewModel::updateSociality,
        onAccommodationChange = viewModel::updateAccommodation,
        onFoodChange = viewModel::updateFood,
        onExperienceChange = viewModel::updateExperience,
        onTransportationChange = viewModel::updateTransportation,
        onCafeExhibitionChange = viewModel::updateCafeExhibition,
        onNextStep = viewModel::nextStep,
        onRestartDiagnosis = viewModel::restartDiagnosis,
        onCourseRecommendationClicked = viewModel::onCourseRecommendationClicked,
        modifier = modifier,
    )
}

@Composable
private fun PropensityScreenContent(
    state: PropensityUiState,
    onBack: () -> Unit,
    onLocalityChange: (Int) -> Unit,
    onFrugalityChange: (Int) -> Unit,
    onExperientialityChange: (Int) -> Unit,
    onVitalityChange: (Int) -> Unit,
    onSocialityChange: (Int) -> Unit,
    onAccommodationChange: (Int) -> Unit,
    onFoodChange: (Int) -> Unit,
    onExperienceChange: (Int) -> Unit,
    onTransportationChange: (Int) -> Unit,
    onCafeExhibitionChange: (Int) -> Unit,
    onNextStep: () -> Unit,
    onRestartDiagnosis: () -> Unit,
    onCourseRecommendationClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage)
        }
    }

    LaunchedEffect(state.step) {
        scrollState.animateScrollTo(0)
    }

    val title = when (state.step) {
        PropensityStep.Preference -> stringResource(R.string.propensity_title_preference)
        PropensityStep.ValueConsumption -> stringResource(R.string.propensity_title_value_consumption)
        PropensityStep.Result -> stringResource(R.string.propensity_title_result)
    }
    val currentStepNumber = when (state.step) {
        PropensityStep.Preference -> 1
        PropensityStep.ValueConsumption -> 2
        PropensityStep.Result -> 3
    }

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            PropensityBrandBar(
                title = title,
                onBackClick = onBack,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BodyBackground)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 22.dp, vertical = 20.dp),
            ) {
                LbStepIndicator(
                    currentStep = currentStepNumber,
                    totalSteps = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )

                Spacer(modifier = Modifier.height(20.dp))

                PropensityHeader(step = state.step)

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(width = 1.dp, color = Border, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                ) {
                    when (state.step) {
                        PropensityStep.Preference -> PreferenceStepContent(
                            state = state,
                            onLocalityChange = onLocalityChange,
                            onFrugalityChange = onFrugalityChange,
                            onExperientialityChange = onExperientialityChange,
                            onVitalityChange = onVitalityChange,
                            onSocialityChange = onSocialityChange,
                            onNextStep = onNextStep,
                        )
                        PropensityStep.ValueConsumption -> ValueConsumptionStepContent(
                            state = state,
                            onAccommodationChange = onAccommodationChange,
                            onFoodChange = onFoodChange,
                            onExperienceChange = onExperienceChange,
                            onTransportationChange = onTransportationChange,
                            onCafeExhibitionChange = onCafeExhibitionChange,
                            onBack = onBack,
                            onNextStep = onNextStep,
                        )
                        PropensityStep.Result -> ResultStepContent(
                            state = state,
                            onRestartDiagnosis = onRestartDiagnosis,
                            onCourseRecommendationClicked = onCourseRecommendationClicked,
                        )
                    }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Brand)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropensityBrandBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_back),
                        contentDescription = stringResource(R.string.propensity_back_content_description),
                        tint = Color.Unspecified,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
    }
}

@Composable
private fun PropensityHeader(step: PropensityStep, modifier: Modifier = Modifier) {
    val (title, subtitle) = when (step) {
        PropensityStep.Preference ->
            stringResource(R.string.propensity_header_preference_title) to
                stringResource(R.string.propensity_header_preference_subtitle)
        PropensityStep.ValueConsumption ->
            stringResource(R.string.propensity_header_value_consumption_title) to
                stringResource(R.string.propensity_header_value_consumption_subtitle)
        PropensityStep.Result ->
            stringResource(R.string.propensity_header_result_title) to
                stringResource(R.string.propensity_header_result_subtitle)
    }
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
    onLocalityChange: (Int) -> Unit,
    onFrugalityChange: (Int) -> Unit,
    onExperientialityChange: (Int) -> Unit,
    onVitalityChange: (Int) -> Unit,
    onSocialityChange: (Int) -> Unit,
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
            onValueChange = onLocalityChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_frugality_title),
            leftLabel = stringResource(R.string.propensity_axis_frugality_left),
            leftSub = stringResource(R.string.propensity_axis_frugality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_frugality_right),
            rightSub = stringResource(R.string.propensity_axis_frugality_right_sub),
            value = state.frugality,
            onValueChange = onFrugalityChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_experientiality_title),
            leftLabel = stringResource(R.string.propensity_axis_experientiality_left),
            leftSub = stringResource(R.string.propensity_axis_experientiality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_experientiality_right),
            rightSub = stringResource(R.string.propensity_axis_experientiality_right_sub),
            value = state.experientiality,
            onValueChange = onExperientialityChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_vitality_title),
            leftLabel = stringResource(R.string.propensity_axis_vitality_left),
            leftSub = stringResource(R.string.propensity_axis_vitality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_vitality_right),
            rightSub = stringResource(R.string.propensity_axis_vitality_right_sub),
            value = state.vitality,
            onValueChange = onVitalityChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_axis_sociality_title),
            leftLabel = stringResource(R.string.propensity_axis_sociality_left),
            leftSub = stringResource(R.string.propensity_axis_sociality_left_sub),
            rightLabel = stringResource(R.string.propensity_axis_sociality_right),
            rightSub = stringResource(R.string.propensity_axis_sociality_right_sub),
            value = state.sociality,
            onValueChange = onSocialityChange,
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
    onAccommodationChange: (Int) -> Unit,
    onFoodChange: (Int) -> Unit,
    onExperienceChange: (Int) -> Unit,
    onTransportationChange: (Int) -> Unit,
    onCafeExhibitionChange: (Int) -> Unit,
    onBack: () -> Unit,
    onNextStep: () -> Unit,
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
            onValueChange = onAccommodationChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_food),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.food,
            onValueChange = onFoodChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_experience),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.experience,
            onValueChange = onExperienceChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_transportation),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.transportation,
            onValueChange = onTransportationChange,
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
        AxisRow(
            title = stringResource(R.string.propensity_item_cafe_exhibition),
            leftLabel = save,
            leftSub = saveEn,
            rightLabel = spend,
            rightSub = spendEn,
            value = state.cafeExhibition,
            onValueChange = onCafeExhibitionChange,
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
                onClick = onNextStep,
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
    onRestartDiagnosis: () -> Unit,
    onCourseRecommendationClicked: () -> Unit,
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
                onClick = onRestartDiagnosis,
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
                onClick = onCourseRecommendationClicked,
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
    PropensityScreenContent(
        state = PropensityUiState(step = PropensityStep.Preference),
        onBack = {},
        onLocalityChange = {},
        onFrugalityChange = {},
        onExperientialityChange = {},
        onVitalityChange = {},
        onSocialityChange = {},
        onAccommodationChange = {},
        onFoodChange = {},
        onExperienceChange = {},
        onTransportationChange = {},
        onCafeExhibitionChange = {},
        onNextStep = {},
        onRestartDiagnosis = {},
        onCourseRecommendationClicked = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PropensityValueConsumptionPreview() {
    PropensityScreenContent(
        state = PropensityUiState(step = PropensityStep.ValueConsumption),
        onBack = {},
        onLocalityChange = {},
        onFrugalityChange = {},
        onExperientialityChange = {},
        onVitalityChange = {},
        onSocialityChange = {},
        onAccommodationChange = {},
        onFoodChange = {},
        onExperienceChange = {},
        onTransportationChange = {},
        onCafeExhibitionChange = {},
        onNextStep = {},
        onRestartDiagnosis = {},
        onCourseRecommendationClicked = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PropensityResultPreview() {
    PropensityScreenContent(
        state = PropensityUiState(
            step = PropensityStep.Result,
            resultType = "실속형 로컬 체험 여행자",
            resultDescription = "럭셔리보다 실속을, 유명 명소보다 골목 상권을, 눈으로 보는 관람보다 직접 해보는 체험을 즐기는 홀로 떠나는 1인 여행자예요.",
        ),
        onBack = {},
        onLocalityChange = {},
        onFrugalityChange = {},
        onExperientialityChange = {},
        onVitalityChange = {},
        onSocialityChange = {},
        onAccommodationChange = {},
        onFoodChange = {},
        onExperienceChange = {},
        onTransportationChange = {},
        onCafeExhibitionChange = {},
        onNextStep = {},
        onRestartDiagnosis = {},
        onCourseRecommendationClicked = {},
    )
}
