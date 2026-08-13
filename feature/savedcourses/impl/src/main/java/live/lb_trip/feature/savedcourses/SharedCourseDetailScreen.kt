package live.lb_trip.feature.savedcourses

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbAudioPlayer
import live.lb_trip.core.designsystem.component.LbBenefitRow
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.core.designsystem.component.LbTimeline
import live.lb_trip.core.designsystem.component.LbTimelineStop
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.util.formatAudioPosition
import live.lb_trip.domain.model.TravelStatus

@Composable
internal fun SharedCourseDetailScreen(
    token: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SharedCourseDetailViewModel = hiltViewModel(
        key = "shared-course-detail-$token",
    ) { factory: SharedCourseDetailViewModel.Factory -> factory.create(token) },
    onIntent: (SharedCourseDetailIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val tokenNotFoundMessage = stringResource(R.string.sharedcourse_error_token_not_found)
    val tokenExpiredMessage = stringResource(R.string.sharedcourse_error_token_expired)
    val loadErrorMessage = stringResource(R.string.sharedcourse_error_load)
    val retryActionLabel = stringResource(R.string.savedcourses_action_retry)
    val mapAppUnavailableMessage = stringResource(R.string.sharedcourse_map_app_unavailable)

    LifecycleStartEffect(Unit) {
        onStopOrDispose {
            onIntent(SharedCourseDetailIntent.AudioPlaybackStopRequested)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SharedCourseDetailSideEffect.OpenBenefitUrl -> uriHandler.openUri(effect.url)
                is SharedCourseDetailSideEffect.OpenMap -> {
                    val uri = Uri.parse(
                        "geo:${effect.latitude},${effect.longitude}" +
                            "?q=${effect.latitude},${effect.longitude}(${Uri.encode(effect.label)})",
                    )
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                        .onFailure { snackbarHostState.showSnackbar(mapAppUnavailableMessage) }
                }
                is SharedCourseDetailSideEffect.ShowLoadError -> {
                    val message = when (effect.reason) {
                        SharedCourseDetailLoadErrorReason.TokenNotFound -> tokenNotFoundMessage
                        SharedCourseDetailLoadErrorReason.TokenExpired -> tokenExpiredMessage
                        SharedCourseDetailLoadErrorReason.Unknown -> loadErrorMessage
                    }
                    val actionLabel = if (effect.reason == SharedCourseDetailLoadErrorReason.Unknown) retryActionLabel else null
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = actionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(SharedCourseDetailIntent.Retry)
                    }
                }
            }
        }
    }

    SharedCourseDetailScreenContent(
        state = state,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBack = onBack,
        onIntent = onIntent,
        modifier = modifier,
    )
}

@Composable
private fun SharedCourseDetailScreenContent(
    state: SharedCourseDetailUiState,
    snackbarHost: @Composable () -> Unit,
    onBack: () -> Unit,
    onIntent: (SharedCourseDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(OkHttpNetworkFetcherFactory()) }.build()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.sharedcourse_back_content_description),
                title = stringResource(R.string.sharedcourse_title),
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.ScreenBg,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (!state.isLoading && !state.loadFailed) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                ) {
                    SharedCourseFromRow(
                        sharedByName = state.sharedByName,
                        status = state.status,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SharedCourseHero(
                        imageUrl = state.imageUrl,
                        imageLoader = imageLoader,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_pin),
                            contentDescription = null,
                            tint = LbColors.Ink3,
                            modifier = Modifier.size(13.dp),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = state.regionName, color = LbColors.Ink2, fontSize = 11.5.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.title,
                        color = LbColors.Ink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    SharedCourseMetas(state = state, modifier = Modifier.padding(horizontal = 16.dp))

                    HorizontalDivider(
                        color = LbColors.LineSoft,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                    )

                    Text(
                        text = stringResource(R.string.sharedcourse_section_course_order),
                        color = LbColors.Ink,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 14.dp),
                    )

                    LbTimeline(
                        stops = state.stops.map {
                            LbTimelineStop(
                                order = it.order,
                                name = it.name,
                                description = it.description,
                                walkDuration = it.walkToNextMinutes?.let { minutes -> "${minutes}분" },
                                hasAudioGuide = it.hasAudioGuide,
                            )
                        },
                        expandedIndices = state.expandedStopIndices,
                        audioGuideLabel = stringResource(R.string.sharedcourse_audio_guide_label),
                        walkDurationLabel = { walkDuration ->
                            stringResource(R.string.sharedcourse_walk_time_template, walkDuration)
                        },
                        onToggle = { onIntent(SharedCourseDetailIntent.StopToggled(it)) },
                        detailHeader = { index ->
                            SharedCourseStopImage(imageUrl = state.stops.getOrNull(index)?.imageUrl, imageLoader = imageLoader)
                        },
                        detailExtraContent = { index ->
                            state.stops.getOrNull(index)?.let { stop ->
                                SharedCourseStopGeoRow(
                                    stop = stop,
                                    onNavigateClick = {
                                        onIntent(
                                            SharedCourseDetailIntent.NavigateClicked(
                                                latitude = stop.latitude,
                                                longitude = stop.longitude,
                                                label = stop.name,
                                            ),
                                        )
                                    },
                                )
                            }
                        },
                        audioContent = { index ->
                            LbAudioPlayer(
                                isPlaying = state.playingStopIndex == index && state.isAudioPlaying,
                                onPlayPauseClick = { onIntent(SharedCourseDetailIntent.PlaybackToggled(index)) },
                                playContentDescription = stringResource(R.string.sharedcourse_content_description_play),
                                pauseContentDescription = stringResource(R.string.sharedcourse_content_description_pause),
                                positionLabel = if (state.playingStopIndex == index) {
                                    formatAudioPosition(state.audioPositionMs, state.audioDurationMs)
                                } else {
                                    formatAudioPosition(0, 0)
                                },
                            )
                        },
                        detailBottomPadding = 7.dp,
                    )

                    if (state.benefits.isNotEmpty()) {
                        HorizontalDivider(
                            color = LbColors.LineSoft,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                        )
                        Text(
                            text = stringResource(R.string.sharedcourse_section_benefits),
                            color = LbColors.Ink,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column {
                            state.benefits.fastForEachIndexed { index, benefit ->
                                if (index > 0) HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                                LbBenefitRow(
                                    title = benefit.title,
                                    description = benefit.description,
                                    onClick = { onIntent(SharedCourseDetailIntent.BenefitClicked(benefit.url)) },
                                    horizontalPadding = 16.dp,
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.sharedcourse_readonly_note),
                        color = LbColors.Ink2,
                        fontSize = 11.5.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                    )
                }
            }

            if (state.isLoading) {
                LbLoadingOverlay()
            }
        }
    }
}

@Composable
private fun SharedCourseFromRow(sharedByName: String, status: TravelStatus, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(LbColors.Paper)
            .padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_share),
            contentDescription = null,
            tint = LbColors.GreenForest,
            modifier = Modifier.size(15.dp),
        )
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = stringResource(R.string.sharedcourse_from_template, sharedByName),
            color = LbColors.Ink2,
            fontSize = 12.5.sp,
            modifier = Modifier.weight(1f),
        )
        SharedCourseStatusBadge(status = status)
    }
}

@Composable
private fun SharedCourseStatusBadge(status: TravelStatus, modifier: Modifier = Modifier) {
    val label = when (status) {
        TravelStatus.BEFORE_TRIP -> stringResource(R.string.savedcourses_status_before_trip)
        TravelStatus.TRAVELING -> stringResource(R.string.savedcourses_status_traveling)
        TravelStatus.COMPLETED -> stringResource(R.string.savedcourses_status_completed)
    }
    Text(
        text = label,
        color = LbColors.Green,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LbColors.GreenTint2)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun SharedCourseHero(imageUrl: String?, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.linearGradient(listOf(LbColors.GreenTint2, LbColors.GreenBlock))),
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SharedCourseMetas(state: SharedCourseDetailUiState, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SharedCourseMeta(
            value = stringResource(R.string.sharedcourse_meta_places_value, state.stops.size),
            label = stringResource(R.string.sharedcourse_meta_places_label),
            modifier = Modifier.weight(1f),
        )
        SharedCourseMeta(
            value = stringResource(R.string.sharedcourse_meta_walk_value, state.walkTotalMinutes),
            label = stringResource(R.string.sharedcourse_meta_walk_label),
            modifier = Modifier.weight(1f),
        )
        SharedCourseMeta(
            value = stringResource(R.string.sharedcourse_meta_audio_value, state.audioGuideCount),
            label = stringResource(R.string.sharedcourse_meta_audio_label),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SharedCourseMeta(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = value, color = LbColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = LbColors.Ink2, fontSize = 11.5.sp)
    }
}

@Composable
private fun SharedCourseStopImage(imageUrl: String?, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxWidth().height(124.dp).background(LbColors.SurfaceSoft),
        )
    } else {
        Box(modifier = modifier.fillMaxWidth().height(124.dp).background(LbColors.SurfaceSoft))
    }
}

@Composable
private fun SharedCourseStopGeoRow(stop: SharedCourseStop, onNavigateClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(top = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_pin),
            contentDescription = null,
            tint = LbColors.Ink3,
            modifier = Modifier.size(12.dp),
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "%.6f, %.6f".format(stop.latitude, stop.longitude),
            color = LbColors.Ink2,
            fontSize = 10.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(9.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .background(LbColors.GreenTint)
                .border(width = 1.dp, color = LbColors.GreenLine, shape = CircleShape)
                .clickable(onClick = onNavigateClick)
                .padding(horizontal = 11.dp, vertical = 7.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_navigation),
                contentDescription = null,
                tint = LbColors.GreenDeep,
                modifier = Modifier.size(13.dp),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.sharedcourse_navigate_cta),
                color = LbColors.GreenForest,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
