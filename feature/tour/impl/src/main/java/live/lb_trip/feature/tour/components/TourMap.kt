package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PolylineOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.collections.immutable.ImmutableList
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.tour.TourIntent
import live.lb_trip.feature.tour.TourStop

private const val DEFAULT_ZOOM = 20.0
private val DashedPolylinePattern = arrayOf(4.dp, 2.dp)

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun TourMap(
    stops: ImmutableList<TourStop>,
    currentStopIndex: Int,
    onIntent: (TourIntent) -> Unit,
    mapContentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val points = stops.map { LatLng(it.latitude, it.longitude) }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(points, currentStopIndex) {
        val focused = points.getOrNull(currentStopIndex) ?: points.firstOrNull() ?: return@LaunchedEffect
        val target = CameraPosition(focused, DEFAULT_ZOOM)
        cameraPositionState.animate(CameraUpdate.toCameraPosition(target), animation = CameraAnimation.Fly)
    }

    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        contentPadding = mapContentPadding,
    ) {
        points.zipWithNext().forEachIndexed { index, (start, end) ->
            key("segment_$index") {
                val isPassed = index < currentStopIndex
                PolylineOverlay(
                    coords = listOf(start, end),
                    color = LbColors.GreenPale,
                    width = 3.dp,
                    pattern = if (isPassed) emptyArray() else DashedPolylinePattern,
                )
            }
        }
        stops.fastForEachIndexed { index, stop ->
            key("${stop.name}_${stop.latitude}_${stop.longitude}") {
                val markerColor = when {
                    index < currentStopIndex -> LbColors.Green
                    index == currentStopIndex -> LbColors.GreenDk
                    else -> LbColors.Ink4
                }
                MarkerComposable(
                    stop.order,
                    markerColor,
                    state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                    captionText = "${stop.order}. ${stop.name}",
                    anchor = Offset(0.5f, 0.5f),
                    onClick = {
                        onIntent(TourIntent.StopSelected(index))
                        true
                    },
                ) {
                    TourMapMarker(order = stop.order, color = markerColor)
                }
            }
        }
    }
}

@Composable
private fun TourMapMarker(order: Int, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = order.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
