package live.lb_trip.feature.propensity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.component.LbScaleSelector

@Composable
internal fun AxisRow(
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
