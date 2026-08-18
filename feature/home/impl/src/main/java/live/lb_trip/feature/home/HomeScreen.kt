package live.lb_trip.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.domain.model.HeroItem
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.ProfileSummary
import live.lb_trip.domain.model.ProfileType
import live.lb_trip.feature.home.components.IncentiveCard

private val HeroScrimTop = Color(0xE6122A20)
private val HeroScrimBottom = Color(0x33163524)
private val HeroChipBackground = Color(0x33FFFFFF)
private val HeroGhostBackground = Color(0x21FFFFFF)

@Composable
fun HomeTabContent(
    onStartDiagnosisClick: () -> Unit,
    onNavigateToSignin: () -> Unit,
    onSavedAllClick: () -> Unit,
    onCourseClick: (Long) -> Unit,
    onPopularCourseClick: (Long) -> Unit,
    onPolicyAllClick: () -> Unit,
    onNavigateToRecommendedRegion: (Long, String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onIntent: (HomeIntent) -> Unit = viewModel::onIntent,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val loadErrorMessage = stringResource(R.string.home_error_courses_load)
    val retryActionLabel = stringResource(R.string.home_action_retry)
    val openUrlFailedMessage = stringResource(R.string.home_error_open_url_failed)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                HomeSideEffect.ShowFeedLoadError -> {
                    val result = snackbarHostState.showSnackbar(message = loadErrorMessage, actionLabel = retryActionLabel)
                    if (result == SnackbarResult.ActionPerformed) {
                        onIntent(HomeIntent.Retry)
                    }
                }
                is HomeSideEffect.OpenUrl -> {
                    runCatching { uriHandler.openUri(effect.url) }
                        .onFailure { snackbarHostState.showSnackbar(openUrlFailedMessage) }
                }
                is HomeSideEffect.NavigateToRecommendedRegion ->
                    onNavigateToRecommendedRegion(effect.regionId, effect.regionName)
            }
        }
    }

    HomeTabContentBody(
        state = state,
        onDiagnosisClick = { if (state.isLoggedIn) onStartDiagnosisClick() else onNavigateToSignin() },
        onNavigateToSignin = onNavigateToSignin,
        onSavedAllClick = onSavedAllClick,
        onPolicyAllClick = onPolicyAllClick,
        onPopularCourseClick = onPopularCourseClick,
        onIncentiveClick = { onIntent(HomeIntent.IncentiveClicked(it)) },
        onFeedItemClick = { item ->
            when (item) {
                is HomeFeedItem.SavedCourseItem -> onCourseClick(item.id)
                is HomeFeedItem.RecommendedRegionItem -> onIntent(HomeIntent.FeedItemClicked(item))
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun HomeTabContentBody(
    state: HomeUiState,
    onDiagnosisClick: () -> Unit,
    onNavigateToSignin: () -> Unit,
    onSavedAllClick: () -> Unit,
    onPolicyAllClick: () -> Unit,
    onPopularCourseClick: (Long) -> Unit,
    onIncentiveClick: (HomeIncentiveCard) -> Unit,
    onFeedItemClick: (HomeFeedItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(OkHttpNetworkFetcherFactory()) }.build()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp),
    ) {
        HomeHero(
            heroItems = state.heroItems,
            imageLoader = imageLoader,
            isLoggedIn = state.isLoggedIn,
            onDiagnosisClick = onDiagnosisClick,
            onPolicyAllClick = onPolicyAllClick,
        )

        if (state.isDiagnosed && state.profileSummary != null) {
            HomeMyTypeSection(summary = state.profileSummary, imageLoader = imageLoader, onRetakeClick = onDiagnosisClick)
        } else if (!state.isTypeSectionLoading) {
            if (state.isLoggedIn) {
                HomeNoTypeCard(types = state.profileTypes, imageLoader = imageLoader, onStartClick = onDiagnosisClick)
            } else {
                HomeTypeStripSection(
                    types = state.profileTypes,
                    imageLoader = imageLoader,
                    isLoggedIn = state.isLoggedIn,
                    onStartClick = onDiagnosisClick,
                )
            }
        }

        HomeIncentiveSection(
            isLoading = state.isIncentivesLoading,
            cards = state.incentiveCards,
            onCardClick = onIncentiveClick,
            onViewAllClick = onPolicyAllClick,
        )

        HomeFeedSection(
            state = state,
            imageLoader = imageLoader,
            onSavedAllClick = onSavedAllClick,
            onFeedItemClick = onFeedItemClick,
            onPopularCourseClick = onPopularCourseClick,
        )
    }
}

@Composable
private fun HomeHero(
    heroItems: ImmutableList<HeroItem>,
    imageLoader: ImageLoader,
    isLoggedIn: Boolean,
    onDiagnosisClick: () -> Unit,
    onPolicyAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val heroItem = heroItems.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp)),
    ) {
        if (heroItem != null) {
            AsyncImage(
                model = heroItem.imageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Brush.linearGradient(listOf(LbColors.GreenDeep, LbColors.GreenDk))),
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(HeroScrimTop, HeroScrimBottom))),
        )
        Column(modifier = Modifier.padding(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 20.dp)) {
            Text(
                text = stringResource(R.string.home_hero_eyebrow),
                color = Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.06.em,
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(HeroChipBackground)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            )
            Text(
                text = stringResource(R.string.home_hero_title),
                color = Color.White,
                fontSize = 21.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).em,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.home_hero_description),
                color = Color(0xD1FFFFFF),
                fontSize = 12.5.sp,
                lineHeight = 19.4.sp,
                modifier = Modifier.padding(bottom = 17.dp),
            )
            LbButton(
                onClick = onDiagnosisClick,
                colors = LbButtonDefaults.whiteColors(),
                shape = RoundedCornerShape(13.dp),
                elevation = null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
            ) {
                Text(
                    text = stringResource(
                        if (isLoggedIn) R.string.home_hero_cta_diagnosis else R.string.home_hero_cta_diagnosis_guest,
                    ),
                    color = LbColors.GreenForest,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = LbColors.GreenForest,
                    modifier = Modifier.padding(start = 8.dp).size(18.dp),
                )
            }
            LbButton(
                onClick = onPolicyAllClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HeroGhostBackground,
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(13.dp),
                elevation = null,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = 9.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_hero_cta_policies),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (heroItem != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HeroChipBackground)
                        .padding(horizontal = 13.dp, vertical = 11.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = stringResource(R.string.home_hero_pick_template, heroItem.title),
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingLabel: String? = null,
    onTrailingClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp),
    ) {
        Text(
            text = title,
            color = LbColors.Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (trailingLabel != null && onTrailingClick != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onTrailingClick)
                    .padding(4.dp),
            ) {
                Text(text = trailingLabel, color = LbColors.Ink3, fontSize = 12.sp, maxLines = 1, softWrap = false)
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = LbColors.Ink3,
                    modifier = Modifier.padding(start = 2.dp).size(13.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeTypeStripSection(
    types: ImmutableList<ProfileType>,
    imageLoader: ImageLoader,
    isLoggedIn: Boolean,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (types.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        HomeSectionHeader(
            title = stringResource(R.string.home_type_section_title),
            trailingLabel = stringResource(
                if (isLoggedIn) R.string.home_type_section_cta else R.string.home_type_section_cta_guest,
            ),
            onTrailingClick = onStartClick,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            items(types, key = { it.code }) { type ->
                HomeTypeCard(type = type, imageLoader = imageLoader, onClick = onStartClick)
            }
        }
    }
}

@Composable
private fun HomeTypeCard(type: ProfileType, imageLoader: ImageLoader, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .width(148.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(LbColors.Paper)
            .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        AsyncImage(
            model = type.imageUrl,
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(LbColors.GreenTint),
        )
        Text(
            text = type.nickname,
            color = LbColors.Ink,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = type.description,
            color = LbColors.Ink3,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HomeMyTypeSection(
    summary: ProfileSummary,
    imageLoader: ImageLoader,
    onRetakeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HomeSectionHeader(
            title = stringResource(R.string.home_mytype_section_title),
            trailingLabel = stringResource(R.string.home_mytype_retake),
            onTrailingClick = onRetakeClick,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(LbColors.Paper)
                .border(1.dp, LbColors.Line, RoundedCornerShape(16.dp))
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = summary.imageUrl,
                    contentDescription = null,
                    imageLoader = imageLoader,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(LbColors.GreenTint),
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = summary.type, color = LbColors.GreenForest, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(R.string.home_mytype_diagnosed_date_template, summary.diagnosedAt.replace('-', '.')),
                        color = LbColors.Ink3,
                        fontSize = 10.5.sp,
                    )
                }
            }
            Text(text = summary.description, color = LbColors.Ink2, fontSize = 12.5.sp, lineHeight = 19.sp)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                summary.sliders.forEach { slider ->
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = slider.minLabel, color = LbColors.Ink3, fontSize = 11.sp)
                            Box(modifier = Modifier.weight(1f))
                            Text(text = slider.maxLabel, color = LbColors.Ink3, fontSize = 11.sp)
                        }
                        HomeSliderBar(score = slider.score, modifier = Modifier.padding(top = 7.dp))
                    }
                }
            }
        }
    }
}

private val SliderDotSize = 13.dp

@Composable
private fun HomeSliderBar(score: Int, modifier: Modifier = Modifier) {
    val fraction = ((score - 1).coerceIn(0, 4) / 4f)
    BoxWithConstraints(modifier = modifier.fillMaxWidth().height(SliderDotSize)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(LbColors.SurfaceSoft),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (maxWidth - SliderDotSize) * fraction)
                .size(SliderDotSize)
                .clip(CircleShape)
                .background(LbColors.GreenLine),
        ) {
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(LbColors.Paper),
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.5.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(LbColors.Green),
                )
            }
        }
    }
}

@Composable
private fun HomeIncentiveSection(
    isLoading: Boolean,
    cards: ImmutableList<HomeIncentiveCard>,
    onCardClick: (HomeIncentiveCard) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isLoading || cards.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        HomeSectionHeader(
            title = stringResource(R.string.home_incentive_section_title),
            trailingLabel = stringResource(R.string.home_incentive_view_all),
            onTrailingClick = onViewAllClick,
        )
        Text(
            text = stringResource(R.string.home_incentive_section_subtitle),
            color = LbColors.Ink3,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 6.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            modifier = Modifier.padding(top = 13.dp),
        ) {
            items(cards.take(4), key = { it.regionName + it.title + it.url }) { card ->
                IncentiveCard(card = card, onClick = { onCardClick(card) }, modifier = Modifier.width(280.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeTabContentBody(
        state = HomeUiState(isHeroLoading = false, isTypeSectionLoading = false, isIncentivesLoading = false, isFeedLoading = false),
        onDiagnosisClick = {},
        onNavigateToSignin = {},
        onSavedAllClick = {},
        onPolicyAllClick = {},
        onPopularCourseClick = {},
        onIncentiveClick = {},
        onFeedItemClick = {},
    )
}
