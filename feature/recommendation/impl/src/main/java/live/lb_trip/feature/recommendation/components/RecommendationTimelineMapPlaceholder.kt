package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

private val PinColor = Color(0xFFAE5C48)

@Composable
internal fun RecommendationTimelineMapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(Brush.linearGradient(listOf(Color(0xFFEEF3EE), Color(0xFFE7EFE9)))),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(22.dp)) {
            val radius = size.minDimension / 2.6f
            val center = Offset(size.width / 2f, size.height / 2f)
            val pinPath = Path().apply {
                addOval(Rect(center = center, radius = radius))
                moveTo(center.x - radius * 0.62f, center.y + radius * 0.62f)
                lineTo(center.x, size.height)
                lineTo(center.x + radius * 0.62f, center.y + radius * 0.62f)
                close()
            }
            drawPath(path = pinPath, color = PinColor)
            drawCircle(color = Color.White, radius = radius * 0.42f, center = center)
        }
    }
}
