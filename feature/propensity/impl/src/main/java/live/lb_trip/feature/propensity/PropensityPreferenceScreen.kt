package live.lb_trip.feature.propensity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.propensity.components.AxisRow
import live.lb_trip.feature.propensity.components.Border
import live.lb_trip.feature.propensity.components.PropensityStepShell

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
