package live.lb_trip.feature.savedcourses

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.domain.model.TravelStatus
import live.lb_trip.feature.savedcourses.components.SavedCourseBenefitRow
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailAppBar
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailCtaBar
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailTabBar
import live.lb_trip.feature.savedcourses.components.SavedCourseReceiptRow
import live.lb_trip.feature.savedcourses.components.SavedCourseShareSheet
import live.lb_trip.feature.savedcourses.components.SavedCourseTimeline

@Composable
internal fun SavedCourseDetailScreen(
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long) -> Unit,
    receiptRegistered: Boolean,
    onReceiptRegisteredConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedCourseDetailViewModel = hiltViewModel(),
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
                        viewModel.onIntent(SavedCourseDetailIntent.Retry)
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
            viewModel.onIntent(SavedCourseDetailIntent.ReceiptRegistered)
            onReceiptRegisteredConsumed()
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
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedCourseDetailScreenContent(
    state: SavedCourseDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (SavedCourseDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { SavedCourseDetailTab.entries.size })
    val context = LocalContext.current
    val view = LocalView.current
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
                hasStops = state.stops.isNotEmpty(),
                isReportAvailable = state.isReportAvailable,
                onTourStartClick = { onIntent(SavedCourseDetailIntent.TourStartClicked) },
                onRegisterReceiptClick = { onIntent(SavedCourseDetailIntent.RegisterReceiptClicked) },
                onShareClick = { showShareSheet = true },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    snackbarHostState.showSnackbar(if (saved) shareSavedMessage else shareFailedMessage)
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
                    if (!shared) snackbarHostState.showSnackbar(shareFailedMessage)
                }
            },
        )
    }
}

@Composable
private fun SavedCourseOrderTab(
    state: SavedCourseDetailUiState,
    onIntent: (SavedCourseDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SavedCourseTimeline(
            stops = state.stops,
            expandedIndices = state.expandedStopIndices,
            onToggle = { onIntent(SavedCourseDetailIntent.StopToggled(it)) },
        )

        if (state.benefits.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp, modifier = Modifier.padding(vertical = 18.dp))
                Text(
                    text = stringResource(R.string.savedcourses_detail_benefits_title),
                    color = LbColors.Ink,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                )
                Column {
                    state.benefits.fastForEachIndexed { index, benefit ->
                        if (index > 0) HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                        SavedCourseBenefitRow(
                            benefit = benefit,
                            onClick = { onIntent(SavedCourseDetailIntent.BenefitClicked(benefit.url)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCourseReceiptTab(state: SavedCourseDetailUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        if (state.isReceiptsLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
            return@Column
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbColors.GreenTint2, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            Text(
                text = stringResource(R.string.savedcourses_detail_receipt_total_label),
                color = LbColors.Ink,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.savedcourses_detail_receipt_amount_template, state.receiptTotalAmount),
                color = LbColors.GreenDk,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (state.receipts.isEmpty()) {
            Text(
                text = stringResource(R.string.savedcourses_detail_receipt_empty),
                color = LbColors.Ink3,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 20.dp),
            )
        } else {
            Column {
                state.receipts.fastForEachIndexed { index, receipt ->
                    key(receipt.receiptId) {
                        if (index > 0) HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                        SavedCourseReceiptRow(
                            receipt = receipt,
                            amountLabel = stringResource(R.string.savedcourses_detail_receipt_amount_template, receipt.amount),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedCourseReportTab(
    state: SavedCourseDetailUiState,
    imageLoader: ImageLoader,
    statusLabel: String,
    placesLabel: String,
    amountLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        if (state.isReportLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
            return@Column
        }
        if (!state.isReportAvailable) {
            Text(
                text = stringResource(R.string.savedcourses_detail_report_not_available),
                color = LbColors.Ink3,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 20.dp),
            )
            return@Column
        }

        SavedCourseReportRow(label = stringResource(R.string.savedcourses_detail_report_status_label), value = statusLabel)
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
        SavedCourseReportRow(label = stringResource(R.string.savedcourses_detail_report_places_label), value = placesLabel)
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
        SavedCourseReportRow(label = stringResource(R.string.savedcourses_detail_report_amount_label), value = amountLabel)

        if (state.reportImageUrl != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.savedcourses_detail_share_preview_label),
                color = LbColors.Ink3,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            AsyncImage(
                model = state.reportImageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
        }
    }
}

@Composable
private fun SavedCourseReportRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 14.dp)) {
        Text(text = label, color = LbColors.Ink3, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(text = value, color = LbColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
        snackbarHostState = remember { SnackbarHostState() },
        onBack = {},
        onIntent = {},
    )
}
