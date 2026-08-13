package live.lb_trip.feature.propensity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbBottomActionButtonRow
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.feature.propensity.components.AxisRow
import live.lb_trip.feature.propensity.components.Border
import live.lb_trip.feature.propensity.components.OutlineBorder
import live.lb_trip.feature.propensity.components.PropensityStepShell

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

        LbBottomActionButtonRow {
            LbBottomActionButton(
                text = stringResource(R.string.propensity_cta_previous_step),
                onClick = onBack,
                colors = LbButtonDefaults.whiteColors(),
                border = BorderStroke(width = 1.dp, color = OutlineBorder),
                modifier = Modifier.weight(1f),
            )
            LbBottomActionButton(
                text = stringResource(R.string.propensity_cta_view_result),
                onClick = { onIntent(PropensityIntent.SubmitAndViewResult) },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp).padding(start = 6.dp),
                    )
                },
            )
        }
    }
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
