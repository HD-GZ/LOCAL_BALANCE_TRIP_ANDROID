package live.lb_trip.core.designsystem.component

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object LbBrush {
    val BottomFadeGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to Color.Transparent,
            0.28f to Color.White,
        ),
    )
}
