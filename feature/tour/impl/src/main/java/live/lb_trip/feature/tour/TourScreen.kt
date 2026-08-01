package live.lb_trip.feature.tour

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.feature.tour.components.TourRouteTimeline
import live.lb_trip.feature.tour.location.rememberFineLocationPermissionGranted
import live.lb_trip.core.designsystem.R as DesignSystemR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TourScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TourViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val retryActionLabel = stringResource(R.string.tour_action_retry)
    val courseNotFoundMessage = stringResource(R.string.tour_error_course_not_found)
    val emptyPlacesMessage = stringResource(R.string.tour_error_empty_places)
    val genericLoadErrorMessage = stringResource(R.string.tour_error_generic_detail)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is TourSideEffect.ShowLoadError -> launch {
                    val message = when (effect.reason) {
                        TourLoadErrorReason.CourseNotFound -> courseNotFoundMessage
                        TourLoadErrorReason.EmptyPlaces -> emptyPlacesMessage
                        TourLoadErrorReason.Unknown -> genericLoadErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(TourIntent.Retry)
                    }
                }

                TourSideEffect.NavigateBack -> onBack()
                TourSideEffect.CollapseSheet -> launch { scaffoldState.bottomSheetState.partialExpand() }
            }
        }
    }

    val view = LocalView.current
    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    val hasLocationPermission = rememberFineLocationPermissionGranted()
    LifecycleStartEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.onIntent(TourIntent.LocationTrackingStarted)
        }
        onStopOrDispose {
            viewModel.onIntent(TourIntent.LocationTrackingStopped)
        }
    }

    TourScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        scaffoldState = scaffoldState,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TourScreenContent(
    state: TourUiState,
    snackbarHostState: SnackbarHostState,
    scaffoldState: BottomSheetScaffoldState,
    onBack: () -> Unit,
    onIntent: (TourIntent) -> Unit,
    modifier: Modifier = Modifier,
) {

    BottomSheetScaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = { TourHeader(regionName = state.regionName, title = state.title, onBackClick = onBack) },
        sheetPeekHeight = SheetPeekHeight,
        sheetContainerColor = LbColors.Paper,
        sheetContent = {
            TourBottomSheetContent(
                stops = state.stops,
                currentStopIndex = state.currentStopIndex,
                onEndTourClick = { onIntent(TourIntent.EndTourClicked) },
                onNextStopClick = { onIntent(TourIntent.NextStopArrived) },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LbColors.Paper,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TourMap(
                stops = state.stops,
                currentStopIndex = state.currentStopIndex,
                onIntent = onIntent,
                mapContentPadding = PaddingValues(bottom = SheetPeekHeight),
            )

            TourProgressChip(
                completedCount = state.currentStopIndex + 1,
                totalCount = state.stops.size,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
            )

            if (state.isLoading) {
                LbLoadingOverlay()
            }
        }
    }
}

@Composable
private fun TourHeader(
    regionName: String,
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = LbColors.Ink3, fontSize = 9.sp, fontWeight = FontWeight.Bold)) {
            append(regionName)
        }
        append("\n")
        withStyle(SpanStyle(color = LbColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)) {
            append(title)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 16.dp),
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_back),
                    contentDescription = stringResource(R.string.tour_back_content_description),
                    tint = Color.Unspecified,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = text)
            }
        }
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
    }
}

@Composable
private fun TourProgressChip(completedCount: Int, totalCount: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.tour_progress_template, completedCount, totalCount),
        color = LbColors.GreenDk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(LbColors.Paper)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun TourMap(
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

@Composable
private fun TourBottomSheetContent(
    stops: ImmutableList<TourStop>,
    currentStopIndex: Int,
    onEndTourClick: () -> Unit,
    onNextStopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLastStop = currentStopIndex >= stops.lastIndex
    val displayedStop = if (isLastStop) stops.getOrNull(currentStopIndex) else stops.getOrNull(currentStopIndex + 1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbColors.Paper)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        if (isLastStop) {
                            R.string.tour_last_stop_label
                        } else {
                            R.string.tour_next_stop_label
                        },
                    ),
                    color = LbColors.Ink3,
                    fontSize = 11.sp,
                )
                Text(
                    text = displayedStop?.name.orEmpty(),
                    color = LbColors.Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LbButton(
                onClick = onEndTourClick,
                colors = LbButtonDefaults.whiteColors(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier,
            ) {
                Text(
                    text = stringResource(R.string.tour_end),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            LbButton(
                onClick = onNextStopClick,
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(
                        if (isLastStop) {
                            R.string.tour_finish
                        } else {
                            R.string.tour_next_stop_arrived
                        },
                    ),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp, modifier = Modifier.padding(top = 22.dp))

        Text(
            text = stringResource(R.string.tour_full_route),
            color = LbColors.Ink,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
        )

        TourRouteTimeline(stops = stops, currentStopIndex = currentStopIndex)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private const val DEFAULT_ZOOM = 20.0
private val SheetPeekHeight = 210.dp
private val DashedPolylinePattern = arrayOf(4.dp, 2.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TourScreenPreview() {
    TourScreenContent(
        state = TourUiState(),
        snackbarHostState = remember { SnackbarHostState() },
        scaffoldState = rememberBottomSheetScaffoldState(),
        onBack = {},
        onIntent = {},
    )
}
