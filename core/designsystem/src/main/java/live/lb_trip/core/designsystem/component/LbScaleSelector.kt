package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import live.lb_trip.core.designsystem.LbColors

private val Track = LbColors.LineSoft
private val SelectedRing = LbColors.GreenTint
private val Brand = LbColors.Green
private val UnselectedBorder = LbColors.Line2
private val ThumbSize = 20.dp
private val DotSize = 10.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LbScaleSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 5,
) {
    Slider(
        value = value.toFloat(),
        onValueChange = { onValueChange(it.roundToInt()) },
        modifier = modifier.fillMaxWidth().height(32.dp),
        valueRange = 1f..steps.toFloat(),
        steps = (steps - 2).coerceAtLeast(0),
        thumb = {
            Box(
                modifier = Modifier.size(ThumbSize).clip(CircleShape).background(SelectedRing),
                contentAlignment = Alignment.Center,
            ) {
                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Brand))
            }
        },
        track = {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val trackWidth = maxWidth
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .background(Track),
                )
                for (step in 0 until steps) {
                    val fraction = if (steps > 1) step / (steps - 1f) else 0f
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = trackWidth * fraction - DotSize / 2)
                            .size(DotSize)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(width = 1.5.dp, color = UnselectedBorder, shape = CircleShape),
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun LbScaleSelectorPreview() {
    LbScaleSelector(value = 3, onValueChange = {}, modifier = Modifier)
}
