package live.lb_trip.feature.tour

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.feature.tour.components.TourBottomSheetContent
import live.lb_trip.feature.tour.components.TourHeader
import live.lb_trip.feature.tour.components.TourMap
import live.lb_trip.feature.tour.components.TourProgressChip
import live.lb_trip.feature.tour.location.rememberActivityRecognitionPermissionGranted
import live.lb_trip.feature.tour.location.rememberFineLocationPermissionGranted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TourScreen(
    onBack: () -> Unit,
    onTourFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TourViewModel = hiltViewModel(),
    onIntent: (TourIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val retryActionLabel = stringResource(R.string.tour_action_retry)
    val courseNotFoundMessage = stringResource(R.string.tour_error_course_not_found)
    val emptyPlacesMessage = stringResource(R.string.tour_error_empty_places)
    val tourStartFailedMessage = stringResource(R.string.tour_error_start_failed)
    val genericLoadErrorMessage = stringResource(R.string.tour_error_generic_detail)
    val endTourErrorMessage = stringResource(R.string.tour_error_end_tour_failed)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is TourSideEffect.ShowLoadError -> launch {
                    val message = when (effect.reason) {
                        TourLoadErrorReason.CourseNotFound -> courseNotFoundMessage
                        TourLoadErrorReason.EmptyPlaces -> emptyPlacesMessage
                        TourLoadErrorReason.TourStartFailed -> tourStartFailedMessage
                        TourLoadErrorReason.Unknown -> genericLoadErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(TourIntent.Retry)
                    }
                }

                TourSideEffect.ShowEndTourError -> launch {
                    val result = snackbarHostState.showSnackbar(message = endTourErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(TourIntent.EndTourClicked)
                    }
                }

                TourSideEffect.NavigateBack -> onTourFinished()
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
            onIntent(TourIntent.LocationTrackingStarted)
        }
        onStopOrDispose {
            onIntent(TourIntent.LocationTrackingStopped)
        }
    }

    val hasActivityRecognitionPermission = rememberActivityRecognitionPermissionGranted()
    LaunchedEffect(hasActivityRecognitionPermission) {
        if (hasActivityRecognitionPermission) {
            onIntent(TourIntent.DistanceRecordingPermissionGranted)
        }
    }

    TourScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        scaffoldState = scaffoldState,
        onBack = onBack,
        onIntent = onIntent,
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
                progressStopIndex = state.furthestStopIndex,
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
                completedCount = state.furthestStopIndex + 1,
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

private val SheetPeekHeight = 210.dp

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
