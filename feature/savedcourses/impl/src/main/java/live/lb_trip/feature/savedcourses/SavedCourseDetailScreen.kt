package live.lb_trip.feature.savedcourses

import android.content.Context
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.util.ImageCompressLevel
import live.lb_trip.core.util.drawInstagramSticker
import live.lb_trip.core.util.getBitmapFromUrl
import live.lb_trip.core.util.instagramStoryShare
import live.lb_trip.core.util.kakaoShare
import live.lb_trip.core.util.kakaoShareReportFeed
import live.lb_trip.core.util.save
import live.lb_trip.core.util.shareReport
import live.lb_trip.core.util.toContentUri
import live.lb_trip.domain.model.TravelStatus
import live.lb_trip.feature.savedcourses.components.ReceiptSourceSheet
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailAppBar
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailCtaBar
import live.lb_trip.feature.savedcourses.components.SavedCourseDetailTabBar
import live.lb_trip.feature.savedcourses.components.SavedCourseOrderTab
import live.lb_trip.feature.savedcourses.components.SavedCourseReceiptTab
import live.lb_trip.feature.savedcourses.components.SavedCourseReportTab
import live.lb_trip.feature.savedcourses.components.SavedCourseShareSheet
import live.lb_trip.feature.savedcourses.components.createReceiptImageUri
import live.lb_trip.feature.savedcourses.components.rememberReportDistanceValueLabel
import live.lb_trip.feature.savedcourses.components.rememberReportMetaLabel

@Composable
internal fun SavedCourseDetailScreen(
    savedCourseId: Long,
    onBack: () -> Unit,
    onNavigateToTour: (Long) -> Unit,
    onNavigateToReceiptCapture: (Long, Uri) -> Unit,
    onNavigateToReceiptDetail: (Long, Long) -> Unit,
    receiptRegistered: Boolean,
    onReceiptRegisteredConsumed: () -> Unit,
    tourEnded: Boolean,
    onTourEndedConsumed: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    viewModel: SavedCourseDetailViewModel = hiltViewModel(
        key = "saved-course-detail-$savedCourseId",
    ) { factory: SavedCourseDetailViewModel.Factory -> factory.create(savedCourseId) },
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
    val kakaoShareFailedMessage = stringResource(R.string.savedcourses_detail_kakao_share_failed)
    val context = LocalContext.current

    val sideEffectMessages = remember(
        loadErrorMessage,
        courseNotFoundMessage,
        emptyPlacesMessage,
        receiptsErrorMessage,
        reportErrorMessage,
        retryActionLabel,
        kakaoShareFailedMessage,
    ) {
        SavedCourseDetailMessages(
            loadError = loadErrorMessage,
            courseNotFound = courseNotFoundMessage,
            emptyPlaces = emptyPlacesMessage,
            receiptsError = receiptsErrorMessage,
            reportError = reportErrorMessage,
            retryAction = retryActionLabel,
            kakaoShareFailed = kakaoShareFailedMessage,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            launch {
                handleSavedCourseDetailSideEffect(
                    effect = effect,
                    context = context,
                    uriHandler = uriHandler,
                    snackbarHostState = snackbarHostState,
                    onIntent = onIntent,
                    onNavigateToTour = onNavigateToTour,
                    messages = sideEffectMessages,
                )
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
        onNavigateToReceiptCapture = onNavigateToReceiptCapture,
        onNavigateToReceiptDetail = onNavigateToReceiptDetail,
        setLoading = viewModel::setLoading,
        modifier = modifier,
        showBackButton = showBackButton,
    )
}

private data class SavedCourseDetailMessages(
    val loadError: String,
    val courseNotFound: String,
    val emptyPlaces: String,
    val receiptsError: String,
    val reportError: String,
    val retryAction: String,
    val kakaoShareFailed: String,
)

private suspend fun handleSavedCourseDetailSideEffect(
    effect: SavedCourseDetailSideEffect,
    context: Context,
    uriHandler: UriHandler,
    snackbarHostState: SnackbarHostState,
    onIntent: (SavedCourseDetailIntent) -> Unit,
    onNavigateToTour: (Long) -> Unit,
    messages: SavedCourseDetailMessages,
) {
    when (effect) {
        is SavedCourseDetailSideEffect.ShowLoadError -> {
            val message = when (effect.reason) {
                SavedCourseDetailLoadErrorReason.CourseNotFound -> messages.courseNotFound
                SavedCourseDetailLoadErrorReason.EmptyPlaces -> messages.emptyPlaces
                SavedCourseDetailLoadErrorReason.Unknown -> messages.loadError
            }
            val result = snackbarHostState.showSnackbar(message = message, actionLabel = messages.retryAction)
            if (result == SnackbarResult.ActionPerformed) {
                onIntent(SavedCourseDetailIntent.Retry)
            }
        }
        SavedCourseDetailSideEffect.ShowReceiptsLoadError -> snackbarHostState.showSnackbar(messages.receiptsError)
        SavedCourseDetailSideEffect.ShowReportLoadError -> snackbarHostState.showSnackbar(messages.reportError)
        is SavedCourseDetailSideEffect.OpenBenefitUrl -> uriHandler.openUri(effect.url)
        is SavedCourseDetailSideEffect.NavigateToTour -> onNavigateToTour(effect.savedCourseId)
        is SavedCourseDetailSideEffect.LaunchKakaoShare -> {
            val feed = kakaoShareReportFeed(
                title = effect.title,
                description = effect.description,
                imageUrl = effect.imageUrl,
                shareToken = effect.shareToken,
            )
            kakaoShare(context, feed)
        }
        SavedCourseDetailSideEffect.ShowKakaoShareError -> snackbarHostState.showSnackbar(messages.kakaoShareFailed)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedCourseDetailScreenContent(
    state: SavedCourseDetailUiState,
    snackbarHost: @Composable () -> Unit,
    onShowSnackbar: suspend (String) -> Unit,
    onBack: () -> Unit,
    onIntent: (SavedCourseDetailIntent) -> Unit,
    onNavigateToReceiptCapture: (Long, Uri) -> Unit,
    onNavigateToReceiptDetail: (Long, Long) -> Unit,
    setLoading: (isLoading: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
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
    val showReceiptSourceSheet = rememberReceiptSourcePicker(
        savedCourseId = state.savedCourseId,
        onNavigateToReceiptCapture = onNavigateToReceiptCapture,
    )
    val shareSavedMessage = stringResource(R.string.savedcourses_detail_share_saved_toast)
    val shareFailedMessage = stringResource(R.string.savedcourses_detail_share_failed)
    val reportMetaLabel = rememberReportMetaLabel(state.reportTourEndedAt)
    val reportPlacesLabel =
        stringResource(R.string.savedcourses_detail_report_places_template, state.reportVisitedPlaceCount)
    val reportAmountLabel =
        stringResource(R.string.savedcourses_detail_receipt_amount_template, state.reportTotalSpentAmount)
    val reportDistanceValueLabel = rememberReportDistanceValueLabel(state.reportDistanceWalkedMeters)
    val currentDate = remember {
        val formatter = SimpleDateFormat("yyyy. MM. dd", Locale.getDefault())
        formatter.format(Date())
    }
    val textMeasurer = rememberTextMeasurer()

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
        topBar = {
            SavedCourseDetailAppBar(
                regionName = state.regionName,
                title = state.title,
                onBackClick = onBack,
                showBackButton = showBackButton,
            )
        },
        bottomBar = {
            SavedCourseDetailCtaBar(
                tab = state.selectedTab,
                status = state.status,
                hasStops = state.stops.isNotEmpty(),
                isReportAvailable = state.isReportAvailable,
                onTourStartClick = { onIntent(SavedCourseDetailIntent.TourStartClicked) },
                onRegisterReceiptClick = showReceiptSourceSheet,
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
                        SavedCourseDetailTab.RECEIPT -> SavedCourseReceiptTab(
                            state = state,
                            onReceiptClick = { receiptId -> onNavigateToReceiptDetail(state.savedCourseId, receiptId) },
                        )

                        SavedCourseDetailTab.REPORT -> SavedCourseReportTab(
                            state = state,
                            imageLoader = imageLoader,
                            metaLabel = reportMetaLabel,
                            placesLabel = reportPlacesLabel,
                            amountLabel = reportAmountLabel,
                            distanceValueLabel = reportDistanceValueLabel,
                        )
                    }
                }
            }
        }
    }

    val distanceLabel = stringResource(R.string.savedcources_detail_story_distance_label)
    val dateLabel = stringResource(R.string.savedcources_detail_story_date_label)
    val appName = stringResource(R.string.savedcources_detail_story_app_name)
    val shareDescription = stringResource(R.string.savedcourses_detail_share_description, state.username)

    if (showShareSheet) {
        SavedCourseShareSheet(
            onDismiss = { showShareSheet = false },
            onSaveImageClick = {
                showShareSheet = false
                setLoading(true)
                coroutineScope.launch {
                    val uri = state.reportImageUrl?.getBitmapFromUrl(context, imageLoader)?.save(context)
                    setLoading(false)
                    onShowSnackbar(if (uri != null) shareSavedMessage else shareFailedMessage)
                }
            },
            onShareClick = {
                showShareSheet = false
                setLoading(true)
                coroutineScope.launch {
                    val uri = state.reportImageUrl?.getBitmapFromUrl(context, imageLoader)?.toContentUri(context, ImageCompressLevel.JPEG_LOW, "lbt_${state.savedCourseId}")
                    setLoading(false)
                    shareReport(context, shareDescription, uri)
                }
            },
            onKakaoShareClick = {
                showShareSheet = false
                onIntent(SavedCourseDetailIntent.KakaoShareClicked(state.title, shareDescription, state.reportImageUrl))
            },
            onInstagramStoryClick = {
                showShareSheet = false
                setLoading(true)
                coroutineScope.launch {
                    val uri = async {
                        state.reportImageUrl?.getBitmapFromUrl(context, imageLoader)
                            ?.toContentUri(context, ImageCompressLevel.PNG, "lbt_${state.savedCourseId}")
                    }
                    val sticker = async {
                        drawInstagramSticker(
                            state.title,
                            "${state.reportDistanceWalkedMeters ?: 0}",
                            distanceLabel,
                            currentDate,
                            dateLabel,
                            appName,
                            textMeasurer
                        ).asAndroidBitmap()
                            .toContentUri(context, ImageCompressLevel.PNG, "lbt_${state.savedCourseId}_sticker")
                    }

                    instagramStoryShare(context, uri.await(), sticker.await())
                    setLoading(false)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberReceiptSourcePicker(
    savedCourseId: Long,
    onNavigateToReceiptCapture: (Long, Uri) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val pickReceiptImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) onNavigateToReceiptCapture(savedCourseId, uri)
    }
    val takeReceiptPicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) onNavigateToReceiptCapture(savedCourseId, uri)
    }

    if (showSheet) {
        ReceiptSourceSheet(
            onDismiss = { showSheet = false },
            onGalleryClick = {
                showSheet = false
                pickReceiptImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onCameraClick = {
                showSheet = false
                val uri = createReceiptImageUri(context)
                pendingCameraUri = uri
                takeReceiptPicture.launch(uri)
            },
        )
    }

    return { showSheet = true }
}

@Composable
private fun SavedCourseDetailTab.toLabel(): String = when (this) {
    SavedCourseDetailTab.COURSE -> stringResource(R.string.savedcourses_detail_tab_course)
    SavedCourseDetailTab.RECEIPT -> stringResource(R.string.savedcourses_detail_tab_receipt)
    SavedCourseDetailTab.REPORT -> stringResource(R.string.savedcourses_detail_tab_report)
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
        onNavigateToReceiptCapture = { _, _ -> },
        onNavigateToReceiptDetail = { _, _ -> },
        setLoading = {}
    )
}
