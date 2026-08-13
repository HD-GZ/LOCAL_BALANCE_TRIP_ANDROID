package live.lb_trip.feature.home

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbAudioPlayer
import live.lb_trip.core.designsystem.component.LbBenefitRow
import live.lb_trip.core.designsystem.component.LbLoadingOverlay
import live.lb_trip.core.designsystem.component.LbTimeline
import live.lb_trip.core.designsystem.component.LbTimelineStop
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.core.util.formatAudioPosition
import live.lb_trip.domain.model.CourseBenefit

@Composable
internal fun PopularCourseDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PopularCourseDetailViewModel = hiltViewModel(),
    onIntent: (PopularCourseDetailIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val snackbarHostState = remember { SnackbarHostState() }
    val retryActionLabel = stringResource(R.string.home_action_retry)
    val courseNotFoundMessage = stringResource(R.string.home_course_detail_error_course_not_found)
    val emptyPlacesMessage = stringResource(R.string.home_course_detail_error_empty_places)
    val genericLoadErrorMessage = stringResource(R.string.home_course_detail_error_generic)
    val mapAppUnavailableMessage = stringResource(R.string.home_course_detail_map_app_unavailable)

    LifecycleStartEffect(Unit) {
        onStopOrDispose {
            onIntent(PopularCourseDetailIntent.AudioPlaybackStopRequested)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is PopularCourseDetailSideEffect.ShowLoadError -> {
                    val message = when (effect.reason) {
                        PopularCourseDetailLoadErrorReason.CourseNotFound -> courseNotFoundMessage
                        PopularCourseDetailLoadErrorReason.EmptyPlaces -> emptyPlacesMessage
                        PopularCourseDetailLoadErrorReason.Unknown -> genericLoadErrorMessage
                    }
                    val result = snackbarHostState.showSnackbar(message = message, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(PopularCourseDetailIntent.Retry)
                    }
                }
                is PopularCourseDetailSideEffect.OpenBenefitUrl -> uriHandler.openUri(effect.url)
                is PopularCourseDetailSideEffect.OpenMap -> {
                    val uri = Uri.parse(
                        "geo:${effect.latitude},${effect.longitude}" +
                            "?q=${effect.latitude},${effect.longitude}(${Uri.encode(effect.label)})",
                    )
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                        .onFailure { snackbarHostState.showSnackbar(mapAppUnavailableMessage) }
                }
            }
        }
    }

    PopularCourseDetailScreenContent(
        state = state,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBack = onBack,
        onIntent = onIntent,
        modifier = modifier,
    )
}

@Composable
private fun PopularCourseDetailScreenContent(
    state: PopularCourseDetailUiState,
    snackbarHost: @Composable () -> Unit,
    onBack: () -> Unit,
    onIntent: (PopularCourseDetailIntent) -> Unit,
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
                backContentDescription = stringResource(R.string.home_course_detail_back_content_description),
                title = state.title.ifBlank { null },
                subtitle = state.regionName.ifBlank { null },
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.ScreenBg,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
            ) {
                PopularCourseDetailHero(
                    imageUrl = state.stops.firstOrNull()?.imageUrl,
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

                PopularCourseDetailMetas(state = state, modifier = Modifier.padding(horizontal = 16.dp))

                HorizontalDivider(
                    color = LbColors.LineSoft,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                )

                Text(
                    text = stringResource(R.string.home_course_detail_section_course_order),
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
                    audioGuideLabel = stringResource(R.string.home_course_detail_audio_guide_label),
                    walkDurationLabel = { walkDuration ->
                        stringResource(R.string.home_course_detail_walk_time_template, walkDuration)
                    },
                    onToggle = { onIntent(PopularCourseDetailIntent.StopToggled(it)) },
                    detailHeader = { index ->
                        PopularCourseStopImage(imageUrl = state.stops.getOrNull(index)?.imageUrl, imageLoader = imageLoader)
                    },
                    detailExtraContent = { index ->
                        state.stops.getOrNull(index)?.let { stop ->
                            PopularCourseStopGeoRow(
                                stop = stop,
                                onNavigateClick = {
                                    onIntent(
                                        PopularCourseDetailIntent.NavigateClicked(
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
                            onPlayPauseClick = { onIntent(PopularCourseDetailIntent.PlaybackToggled(index)) },
                            playContentDescription = stringResource(R.string.home_course_detail_content_description_play),
                            pauseContentDescription = stringResource(R.string.home_course_detail_content_description_pause),
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
                        text = stringResource(R.string.home_course_detail_section_benefits),
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
                                onClick = { onIntent(PopularCourseDetailIntent.BenefitClicked(benefit.url)) },
                                horizontalPadding = 16.dp,
                            )
                        }
                    }
                }
            }

            if (state.isLoading) {
                LbLoadingOverlay()
            }
        }
    }
}

@Composable
private fun PopularCourseDetailHero(imageUrl: String?, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
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
private fun PopularCourseDetailMetas(state: PopularCourseDetailUiState, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PopularCourseDetailMeta(
            value = stringResource(R.string.home_course_detail_meta_places_value, state.stops.size),
            label = stringResource(R.string.home_course_detail_meta_places_label),
            modifier = Modifier.weight(1f),
        )
        PopularCourseDetailMeta(
            value = stringResource(R.string.home_course_detail_meta_walk_value, state.walkTotalMinutes),
            label = stringResource(R.string.home_course_detail_meta_walk_label),
            modifier = Modifier.weight(1f),
        )
        PopularCourseDetailMeta(
            value = stringResource(R.string.home_course_detail_meta_audio_value, state.audioGuideCount),
            label = stringResource(R.string.home_course_detail_meta_audio_label),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PopularCourseDetailMeta(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = value, color = LbColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = LbColors.Ink2, fontSize = 11.5.sp)
    }
}

@Composable
private fun PopularCourseStopImage(imageUrl: String?, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
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
private fun PopularCourseStopGeoRow(stop: PopularCourseStop, onNavigateClick: () -> Unit, modifier: Modifier = Modifier) {
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
            overflow = TextOverflow.Ellipsis,
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
                text = stringResource(R.string.home_course_detail_navigate_cta),
                color = LbColors.GreenForest,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularCourseDetailScreenPreview() {
    PopularCourseDetailScreenContent(
        state = PopularCourseDetailUiState(
            isLoading = false,
            regionName = "전라남도 담양군",
            title = "전라남도 담양군 골목 미식 코스",
            benefits = persistentListOf(CourseBenefit(title = "KTX 인구감소지역 할인", description = "코레일 공식 채널로 이동", url = "https://example.com")),
            expandedStopIndices = persistentSetOf(0),
        ),
        snackbarHost = {},
        onBack = {},
        onIntent = {},
    )
}
