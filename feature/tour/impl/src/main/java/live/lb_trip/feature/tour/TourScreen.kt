package live.lb_trip.feature.tour

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.feature.tour.components.TourActionBar
import live.lb_trip.feature.tour.components.TourBottomSheetContent
import live.lb_trip.feature.tour.components.TourHeader
import live.lb_trip.feature.tour.components.TourMap
import live.lb_trip.feature.tour.components.TourProgressSection
import live.lb_trip.feature.tour.location.rememberActivityRecognitionPermissionGranted
import live.lb_trip.feature.tour.location.rememberFineLocationPermissionGranted

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
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
    val directive = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo())
    val isTwoPane = directive.maxHorizontalPartitions > 1
    val uriHandler = LocalUriHandler.current
    val retryActionLabel = stringResource(R.string.tour_action_retry)
    val courseNotFoundMessage = stringResource(R.string.tour_error_course_not_found)
    val emptyPlacesMessage = stringResource(R.string.tour_error_empty_places)
    val tourStartFailedMessage = stringResource(R.string.tour_error_start_failed)
    val genericLoadErrorMessage = stringResource(R.string.tour_error_generic_detail)
    val endTourErrorMessage = stringResource(R.string.tour_error_end_tour_failed)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            handleTourSideEffect(
                effect = effect,
                isTwoPane = isTwoPane,
                snackbarHostState = snackbarHostState,
                scaffoldState = scaffoldState,
                messages = TourSideEffectMessages(
                    retryActionLabel = retryActionLabel,
                    courseNotFound = courseNotFoundMessage,
                    emptyPlaces = emptyPlacesMessage,
                    tourStartFailed = tourStartFailedMessage,
                    unknown = genericLoadErrorMessage,
                    endTourError = endTourErrorMessage,
                ),
                onIntent = onIntent,
                onTourFinished = onTourFinished,
                onOpenBenefitUrl = uriHandler::openUri,
            )
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
        directive = directive,
        isTwoPane = isTwoPane,
        onBack = onBack,
        onIntent = onIntent,
        modifier = modifier,
    )
}

private data class TourSideEffectMessages(
    val retryActionLabel: String,
    val courseNotFound: String,
    val emptyPlaces: String,
    val tourStartFailed: String,
    val unknown: String,
    val endTourError: String,
)

@Suppress("LongParameterList")
@OptIn(ExperimentalMaterial3Api::class)
private suspend fun CoroutineScope.handleTourSideEffect(
    effect: TourSideEffect,
    isTwoPane: Boolean,
    snackbarHostState: SnackbarHostState,
    scaffoldState: BottomSheetScaffoldState,
    messages: TourSideEffectMessages,
    onIntent: (TourIntent) -> Unit,
    onTourFinished: () -> Unit,
    onOpenBenefitUrl: (String) -> Unit,
) {
    when (effect) {
        is TourSideEffect.ShowLoadError -> launch {
            val message = when (effect.reason) {
                TourLoadErrorReason.CourseNotFound -> messages.courseNotFound
                TourLoadErrorReason.EmptyPlaces -> messages.emptyPlaces
                TourLoadErrorReason.TourStartFailed -> messages.tourStartFailed
                TourLoadErrorReason.Unknown -> messages.unknown
            }
            val result = snackbarHostState.showSnackbar(message = message, actionLabel = messages.retryActionLabel)
            if (result == SnackbarResult.ActionPerformed) {
                onIntent(TourIntent.Retry)
            }
        }

        TourSideEffect.ShowEndTourError -> launch {
            val result = snackbarHostState.showSnackbar(
                message = messages.endTourError,
                actionLabel = messages.retryActionLabel,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onIntent(TourIntent.NextStopArrived)
            }
        }

        is TourSideEffect.OpenBenefitUrl -> onOpenBenefitUrl(effect.url)
        TourSideEffect.NavigateBack -> onTourFinished()
        TourSideEffect.CollapseSheet -> if (!isTwoPane) {
            launch { scaffoldState.bottomSheetState.partialExpand() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun TourScreenContent(
    state: TourUiState,
    snackbarHostState: SnackbarHostState,
    scaffoldState: BottomSheetScaffoldState,
    directive: PaneScaffoldDirective,
    isTwoPane: Boolean,
    onBack: () -> Unit,
    onIntent: (TourIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isTwoPane) {
        TourTwoPaneContent(
            state = state,
            snackbarHostState = snackbarHostState,
            directive = directive,
            onBack = onBack,
            onIntent = onIntent,
            modifier = modifier,
        )
    } else {
        TourCompactContent(
            state = state,
            snackbarHostState = snackbarHostState,
            scaffoldState = scaffoldState,
            onBack = onBack,
            onIntent = onIntent,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TourCompactContent(
    state: TourUiState,
    snackbarHostState: SnackbarHostState,
    scaffoldState: BottomSheetScaffoldState,
    onBack: () -> Unit,
    onIntent: (TourIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var actionBarHeightPx by remember { mutableIntStateOf(0) }
    val actionBarHeight = with(density) { actionBarHeightPx.toDp() }

    Box(modifier = modifier.fillMaxSize()) {
        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = actionBarHeight),
            scaffoldState = scaffoldState,
            topBar = { TourTopBar(state = state, onBackClick = onBack) },
            sheetPeekHeight = SheetPeekHeight,
            sheetContainerColor = LbColors.Paper,
            sheetContent = { TourStopsContent(state = state, onIntent = onIntent) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = LbColors.Paper,
        ) {
            TourMapContent(
                state = state,
                onIntent = onIntent,
                mapContentPadding = PaddingValues(bottom = SheetPeekHeight + actionBarHeight),
            )
        }

        TourActionBar(
            state = state,
            onNextStopClick = { onIntent(TourIntent.NextStopArrived) },
            onFinishAcknowledged = { onIntent(TourIntent.FinishAcknowledged) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { actionBarHeightPx = it.height },
        )
    }
}

@Composable
private fun TourTopBar(state: TourUiState, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        TourHeader(
            regionName = state.regionName,
            title = state.title,
            onBackClick = onBackClick,
        )
        TourProgressSection(
            isFinished = state.isFinished,
            completedCount = (state.furthestStopIndex + 1).coerceAtMost(state.stops.size),
            totalCount = state.stops.size,
            tourStartedAt = state.tourStartedAt,
            elapsedMinutes = state.elapsedMinutes,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun TourTwoPaneContent(
    state: TourUiState,
    snackbarHostState: SnackbarHostState,
    directive: PaneScaffoldDirective,
    onBack: () -> Unit,
    onIntent: (TourIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigator = rememberSupportingPaneScaffoldNavigator(scaffoldDirective = directive)
    val density = LocalDensity.current
    var actionBarHeightPx by remember { mutableIntStateOf(0) }
    val actionBarHeight = with(density) { actionBarHeightPx.toDp() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TourTopBar(state = state, onBackClick = onBack) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        NavigableSupportingPaneScaffold(
            navigator = navigator,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            mainPane = {
                AnimatedPane {
                    TourMapContent(state = state, onIntent = onIntent, mapContentPadding = PaddingValues())
                }
            },
            supportingPane = {
                AnimatedPane {
                    Box(modifier = Modifier.fillMaxHeight().background(LbColors.Paper)) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = actionBarHeight),
                        ) {
                            TourStopsContent(state = state, onIntent = onIntent)
                        }

                        TourActionBar(
                            state = state,
                            onNextStopClick = { onIntent(TourIntent.NextStopArrived) },
                            onFinishAcknowledged = { onIntent(TourIntent.FinishAcknowledged) },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .onSizeChanged { actionBarHeightPx = it.height },
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun TourMapContent(
    state: TourUiState,
    onIntent: (TourIntent) -> Unit,
    mapContentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        TourMap(
            stops = state.stops,
            currentStopIndex = state.currentStopIndex,
            furthestStopIndex = state.furthestStopIndex,
            isFinished = state.isFinished,
            onIntent = onIntent,
            mapContentPadding = mapContentPadding,
        )

        if (state.isLoading) {
            LbLoadingOverlay()
        }
    }
}

@Composable
private fun TourStopsContent(state: TourUiState, onIntent: (TourIntent) -> Unit) {
    TourBottomSheetContent(
        stops = state.stops,
        benefits = state.benefits,
        currentStopIndex = state.currentStopIndex,
        furthestStopIndex = state.furthestStopIndex,
        isFinished = state.isFinished,
        isAudioPlaying = state.isAudioPlaying,
        audioPositionMs = state.audioPositionMs,
        audioDurationMs = state.audioDurationMs,
        onStopClick = { onIntent(TourIntent.StopSelected(it)) },
        onPlaybackToggle = { onIntent(TourIntent.PlaybackToggled) },
        onBenefitClick = { onIntent(TourIntent.BenefitClicked(it)) },
    )
}

private val SheetPeekHeight = 96.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Preview(showBackground = true)
@Composable
private fun TourScreenPreview() {
    TourScreenContent(
        state = TourUiState(),
        snackbarHostState = remember { SnackbarHostState() },
        scaffoldState = rememberBottomSheetScaffoldState(),
        directive = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo()),
        isTwoPane = false,
        onBack = {},
        onIntent = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Preview(showBackground = true, widthDp = 900, heightDp = 500)
@Composable
private fun TourScreenTwoPanePreview() {
    TourScreenContent(
        state = TourUiState(),
        snackbarHostState = remember { SnackbarHostState() },
        scaffoldState = rememberBottomSheetScaffoldState(),
        directive = calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(currentWindowAdaptiveInfo()),
        isTwoPane = true,
        onBack = {},
        onIntent = {},
    )
}
