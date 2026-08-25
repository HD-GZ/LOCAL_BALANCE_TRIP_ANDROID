package live.lb_trip.feature.recommendation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PolylineOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import live.lb_trip.feature.recommendation.CourseStop

private const val MAP_BOUNDS_PADDING_PX = 40
private val PinBorder = Color.White

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun RecommendationCourseMap(stops: List<CourseStop>, modifier: Modifier = Modifier) {
    val points = remember(stops) { stops.map { LatLng(it.latitude, it.longitude) } }
    val cameraPositionState = rememberCameraPositionState()
    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(points, isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        val target = if (points.size > 1) {
            val bounds = LatLngBounds.Builder().apply { points.forEach { include(it) } }.build()
            CameraUpdate.fitBounds(bounds, MAP_BOUNDS_PADDING_PX)
        } else {
            points.firstOrNull()?.let { CameraUpdate.toCameraPosition(CameraPosition(it, DEFAULT_ZOOM)) }
        }
        target?.let { cameraPositionState.move(it) }
    }

    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { isMapLoaded = true },
    ) {
        points.zipWithNext().forEachIndexed { index, (start, end) ->
            key("segment_$index") {
                PolylineOverlay(coords = listOf(start, end), color = RouteColor, width = 3.dp)
            }
        }
        stops.fastForEachIndexed { index, stop ->
            key("${stop.name}_${stop.latitude}_${stop.longitude}") {
                MarkerComposable(
                    state = MarkerState(position = LatLng(stop.latitude, stop.longitude)),
                    captionText = "${stop.order}. ${stop.name}",
                    anchor = Offset(0.5f, 0.5f),
                ) {
                    CourseMapMarker(order = stop.order)
                }
            }
        }
    }
}

@Composable
private fun CourseMapMarker(order: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(RouteColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = order.toString(), color = PinBorder, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

private const val DEFAULT_ZOOM = 14.0
private val RouteColor = Color(0xFF4C8B5B)
