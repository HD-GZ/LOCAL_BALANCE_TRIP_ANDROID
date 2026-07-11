package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors

private val TrackDone = LbColors.GreenLine
private val TrackUpcoming = LbColors.LineSoft
private val CurrentRing = LbColors.GreenTint
private val Brand = LbColors.Green
private val UpcomingBorder = LbColors.Line2
private val UpcomingText = LbColors.Ink3

@Composable
fun LbStepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (step in 1..totalSteps) {
            StepCircle(step = step, currentStep = currentStep)
            if (step != totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.8.dp)
                        .background(
                            color = if (step < currentStep) TrackDone else TrackUpcoming,
                            shape = RoundedCornerShape(2.dp),
                        ),
                )
            }
        }
    }
}

@Composable
private fun StepCircle(step: Int, currentStep: Int) {
    when {
        step < currentStep -> Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(CurrentRing),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Brand,
                modifier = Modifier.size(16.dp),
            )
        }

        step == currentStep -> Box(
            modifier = Modifier.size(42.dp).clip(CircleShape).background(CurrentRing),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(Brand),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = step.toString(),
                    color = Color.White,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        else -> Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(width = 1.7.dp, color = UpcomingBorder, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = step.toString(),
                color = UpcomingText,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LbStepIndicatorPreview() {
    LbStepIndicator(currentStep = 2, totalSteps = 3, modifier = Modifier)
}
