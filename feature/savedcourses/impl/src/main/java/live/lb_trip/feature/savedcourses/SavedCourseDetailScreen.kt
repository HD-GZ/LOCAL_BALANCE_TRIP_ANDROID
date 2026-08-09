package live.lb_trip.feature.savedcourses

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.domain.model.TravelStatus
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailAppBar
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailCtaBar
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailTabBar
import live.lb_trip.feature.savedcourses.components.SavedCourseOrderTab
import live.lb_trip.feature.savedcourses.components.SavedCourseReceiptTab
import live.lb_trip.feature.savedcourses.components.SavedCourseReportTab
import live.lb_trip.feature.savedcourses.components.SavedCourseShareSheet
import live.lb_trip.feature.savedcourses.components.rememberReportMovementLabels

@Composable
internal fun SavedCourseDetailScreen(
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long) -> Unit,
    receiptRegistered: Boolean,
    onReceiptRegisteredConsumed: () -> Unit,
    tourEnded: Boolean,
    onTourEndedConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedCourseDetailViewModel = hiltViewModel(),
    onIntent: (SavedCourseDetailIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.savedcourses_detail_error_load)
    val courseNotFoundMessage = stringResource(R.string.savedcourses_detail_error_course_not_found)
    val emptyPlacesMessage = stringResource(R.string.savedcourses_detail_error_empty_places)
    val receiptsErrorMessage = stringResource(R.string.savedcourses_detail_error_receipts)
    val reportErrorMessage = stringResource(R.string.savedcourses_detail_error_report)
    val retryActionLabel = stringResource(R.string.savedcourses_detail_action_retry)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SavedCourseDetailSideEffect.ShowLoadError -> launch {
                    val message = when (effect.reason) {
                        SavedCourseDetailLoadErrorReason.CourseNotFound -> courseNotFoundMessage
                        SavedCourseDetailLoadErrorReason.EmptyPlaces -> emptyPlacesMessage
                        SavedCourseDetailLoadErrorReason.Unknown -> loadErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(SavedCourseDetailIntent.Retry)
                    }
                }
                SavedCourseDetailSideEffect.ShowReceiptsLoadError -> launch { snackbarHostState.showSnackbar(receiptsErrorMessage) }
                SavedCourseDetailSideEffect.ShowReportLoadError -> launch { snackbarHostState.showSnackbar(reportErrorMessage) }
                is SavedCourseDetailSideEffect.OpenBenefitUrl -> uriHandler.openUri(effect.url)
                is SavedCourseDetailSideEffect.NavigateToTour -> onNavigateToTour(effect.savedCourseId)
                is SavedCourseDetailSideEffect.NavigateToReceiptCapture -> onNavigateToReceiptCapture(effect.savedCourseId)
            }
        }
    }

    LaunchedEffect(receiptRegistered) {
        if (receiptRegistered) {
            onIntent(SavedCourseDetailIntent.ReceiptRegistered)
            onReceiptRegisteredConsumed()
        }
    }

    LaunchedEffect(tourEnded) {
        if (tourEnded) {
            onIntent(SavedCourseDetailIntent.Retry)
            onTourEndedConsumed()
        }
    }

    val activity = LocalActivity.current
    if (activity != null) {
        SideEffect {
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
        }
    }

    SavedCourseDetailScreenContent(
        state = state,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onShowSnackbar = { message -> snackbarHostState.showSnackbar(message) },
        onBack = onBack,
        onIntent = onIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedCourseDetailScreenContent(
    state: SavedCourseDetailUiState,
    snackbarHost: @Composable () -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    onBack: () -> Unit,
    onIntent: (SavedCourseDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { SavedCourseDetailTab.entries.size })
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory()) }
            .build()
    }
    val coroutineScope = rememberCoroutineScope()
    var showShareSheet by remember { mutableStateOf(false) }
    val shareSavedMessage = stringResource(R.string.savedcourses_detail_share_saved_toast)
    val shareFailedMessage = stringResource(R.string.savedcourses_detail_share_failed)
    val shareCardStatusLabel = state.status.toLabel()
    val shareCardPlacesLabel =
        stringResource(R.string.savedcourses_detail_report_places_template, state.reportVisitedPlaceCount)
    val shareCardAmountLabel =
        stringResource(R.string.savedcourses_detail_receipt_amount_template, state.reportTotalSpentAmount)
    val shareCardMovementLabels = rememberReportMovementLabels(state.reportDistanceWalkedMeters, state.reportStepCount)

    LaunchedEffect(state.selectedTab) {
        val targetPage = state.selectedTab.ordinal
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }
    LaunchedEffect(pagerState.settledPage) {
        val pageTab = SavedCourseDetailTab.entries[pagerState.settledPage]
        if (pageTab != state.selectedTab) {
            onIntent(SavedCourseDetailIntent.TabSelected(pageTab))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { SavedCourseDetailAppBar(regionName = state.regionName, title = state.title, onBackClick = onBack) },
        bottomBar = {
            SavedCourseDetailCtaBar(
                tab = state.selectedTab,
                status = state.status,
                hasStops = state.stops.isNotEmpty(),
                isReportAvailable = state.isReportAvailable,
                onTourStartClick = { onIntent(SavedCourseDetailIntent.TourStartClicked) },
                onRegisterReceiptClick = { onIntent(SavedCourseDetailIntent.RegisterReceiptClicked) },
                onShareClick = { showShareSheet = true },
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            SavedCourseDetailTabBar(
                selectedTab = state.selectedTab,
                onTabSelected = { onIntent(SavedCourseDetailIntent.TabSelected(it)) },
                tabLabel = { it.toLabel() },
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 18.dp),
            )

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f).fillMaxWidth()) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                ) {
                    when (SavedCourseDetailTab.entries[page]) {
                        SavedCourseDetailTab.COURSE -> SavedCourseOrderTab(state = state, onIntent = onIntent)
                        SavedCourseDetailTab.RECEIPT -> SavedCourseReceiptTab(state = state)
                        SavedCourseDetailTab.REPORT -> SavedCourseReportTab(
                            state = state,
                            imageLoader = imageLoader,
                            statusLabel = shareCardStatusLabel,
                            placesLabel = shareCardPlacesLabel,
                            amountLabel = shareCardAmountLabel,
                            distanceLabel = shareCardMovementLabels.distance,
                            stepsLabel = shareCardMovementLabels.steps,
                        )
                    }
                }
            }
        }
    }

    if (showShareSheet) {
        SavedCourseShareSheet(
            onDismiss = { showShareSheet = false },
            onSaveImageClick = {
                showShareSheet = false
                val imageUrl = state.reportImageUrl
                coroutineScope.launch {
                    val saved = imageUrl != null && runCatching {
                        val bitmap = downloadReportImageBitmap(imageLoader, context, imageUrl)
                            ?: return@runCatching false
                        saveBitmapToGallery(context, bitmap, shareImageFileName())
                    }.getOrDefault(false)
                    onShowSnackbar(if (saved) shareSavedMessage else shareFailedMessage)
                }
            },
            onShareClick = {
                showShareSheet = false
                val imageUrl = state.reportImageUrl
                coroutineScope.launch {
                    val shared = imageUrl != null && runCatching {
                        val bitmap = downloadReportImageBitmap(imageLoader, context, imageUrl)
                            ?: return@runCatching false
                        shareBitmapImage(context, bitmap, shareImageFileName())
                    }.getOrDefault(false)
                    if (!shared) onShowSnackbar(shareFailedMessage)
                }
            },
        )
    }
}

@Composable
private fun SavedCourseDetailTab.toLabel(): String = when (this) {
    SavedCourseDetailTab.COURSE -> stringResource(R.string.savedcourses_detail_tab_course)
    SavedCourseDetailTab.RECEIPT -> stringResource(R.string.savedcourses_detail_tab_receipt)
    SavedCourseDetailTab.REPORT -> stringResource(R.string.savedcourses_detail_tab_report)
}

@Composable
private fun TravelStatus.toLabel(): String = when (this) {
    TravelStatus.BEFORE_TRIP -> stringResource(R.string.savedcourses_status_before_trip)
    TravelStatus.TRAVELING -> stringResource(R.string.savedcourses_status_traveling)
    TravelStatus.COMPLETED -> stringResource(R.string.savedcourses_status_completed)
}

@Preview(showBackground = true)
@Composable
private fun SavedCourseDetailScreenPreview() {
    SavedCourseDetailScreenContent(
        state = SavedCourseDetailUiState(
            isLoading = false,
            regionName = "전라남도 담양군",
            title = "담양군 자연 체험 힐링 코스",
            status = TravelStatus.BEFORE_TRIP,
            expandedStopIndices = persistentSetOf(0),
        ),
        snackbarHost = {},
        onShowSnackbar = {},
        onBack = {},
        onIntent = {},
    )
}
