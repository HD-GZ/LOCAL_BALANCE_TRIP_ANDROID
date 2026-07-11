package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.feature.recommendation.R

@Composable
internal fun RecommendationFlowStepper(currentStep: Int, modifier: Modifier = Modifier) {
    val captions = listOf(
        stringResource(R.string.recommendation_step_caption_region),
        stringResource(R.string.recommendation_step_caption_course),
        stringResource(R.string.recommendation_step_caption_detail),
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        captions.forEachIndexed { index, caption ->
            val step = index + 1
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 13.dp)
                        .width(24.dp)
                        .height(1.5.dp)
                        .background(
                            color = if (step <= currentStep) GreenLine else LineSoft,
                            shape = RoundedCornerShape(2.dp),
                        ),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(48.dp),
            ) {
                FlowStepCircle(step = step, currentStep = currentStep)
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = caption,
                    fontSize = 10.5.sp,
                    fontWeight = if (step == currentStep) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (step <= currentStep) Green else Ink3,
                )
            }
        }
    }
}

@Composable
private fun FlowStepCircle(step: Int, currentStep: Int) {
    when {
        step < currentStep -> Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(GreenTint)
                .border(1.dp, Green, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(12.dp),
            )
        }

        step == currentStep -> Box(
            modifier = Modifier.size(26.dp).clip(CircleShape).background(Green),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = step.toString(), color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
        }

        else -> Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Paper)
                .border(1.5.dp, Line2, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = step.toString(), color = Ink3, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
