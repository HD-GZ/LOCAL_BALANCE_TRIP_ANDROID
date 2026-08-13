package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
import live.lb_trip.feature.tour.R
import live.lb_trip.feature.tour.TourIntent
import live.lb_trip.feature.tour.TourStop

private const val DEFAULT_ZOOM = 20.0
private val DashedPolylinePattern = arrayOf(4.dp, 2.dp)

@Suppress("LongParameterList")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun TourMap(
    stops: ImmutableList<TourStop>,
    currentStopIndex: Int,
    furthestStopIndex: Int,
    isFinished: Boolean,
    onIntent: (TourIntent) -> Unit,
    mapContentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val points = stops.map { LatLng(it.latitude, it.longitude) }
    val cameraPositionState = rememberCameraPositionState()
    val nextIndex = (furthestStopIndex + 1).coerceAtMost(stops.lastIndex)

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
                val isPassed = index < furthestStopIndex
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
                val isVisited = index <= furthestStopIndex
                val isNext = !isFinished && !isVisited && index == nextIndex
                val markerColor = when {
                    isVisited -> LbColors.Green
                    isNext -> LbColors.RequiredMark
                    else -> LbColors.Ink4
                }
                MarkerComposable(
                    stop.order,
                    markerColor,
                    index == currentStopIndex,
                    state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                    captionText = "${stop.order}. ${stop.name}",
                    anchor = Offset(0.5f, 0.5f),
                    onClick = {
                        onIntent(TourIntent.StopSelected(index))
                        true
                    },
                ) {
                    TourMapMarker(order = stop.order, color = markerColor, isVisited = isVisited, isSelected = index == currentStopIndex)
                }
            }
        }
    }
}

@Composable
private fun TourMapMarker(order: Int, color: Color, isVisited: Boolean, isSelected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) Modifier.border(2.dp, LbColors.GreenTint2, CircleShape) else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isVisited) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp),
            )
        } else {
            Text(text = order.toString(), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
