package live.lb_trip.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.PopularCourse
import live.lb_trip.domain.model.TravelStatus

@Composable
internal fun HomeFeedSection(
    state: HomeUiState,
    imageLoader: ImageLoader,
    onSavedAllClick: () -> Unit,
    onFeedItemClick: (HomeFeedItem) -> Unit,
    onPopularCourseClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isFeedLoading) {
        Box(modifier = modifier.fillMaxWidth().padding(top = 10.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LbColors.Green)
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (!state.isLoggedIn) {
            if (state.popularCourses.isNotEmpty()) {
                Column {
                    HomeSectionHeader(title = stringResource(R.string.home_popular_section_title))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        items(state.popularCourses, key = { it.courseId }) { course ->
                            HomePopularCourseCard(
                                course = course,
                                imageLoader = imageLoader,
                                onClick = { onPopularCourseClick(course.courseId) },
                                modifier = Modifier.width(220.dp),
                            )
                        }
                    }
                }
            }
            return
        }

        if (state.feed.isEmpty()) return

        HomeSectionHeader(
            title = stringResource(R.string.home_feed_section_title),
            trailingLabel = stringResource(R.string.home_feed_view_all),
            onTrailingClick = onSavedAllClick,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            items(state.feed, key = { it.id.toString() + (it is HomeFeedItem.SavedCourseItem) }) { item ->
                HomeFeedCard(
                    item = item,
                    imageLoader = imageLoader,
                    onClick = { onFeedItemClick(item) },
                    modifier = Modifier.width(230.dp),
                )
            }
        }
    }
}

@Composable
private fun String.toTravelStatusLabelOrNull(): String? {
    val status = runCatching { TravelStatus.valueOf(this) }.getOrNull() ?: return null
    return when (status) {
        TravelStatus.BEFORE_TRIP -> stringResource(R.string.home_status_before_trip)
        TravelStatus.TRAVELING -> stringResource(R.string.home_status_traveling)
        TravelStatus.COMPLETED -> stringResource(R.string.home_status_completed)
    }
}

@Composable
private fun HomeFeedCard(item: HomeFeedItem, imageLoader: ImageLoader, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isRecommended = item is HomeFeedItem.RecommendedRegionItem
    val status = item.subtitle?.let { runCatching { TravelStatus.valueOf(it) }.getOrNull() }
    val flagLabel = if (isRecommended) {
        stringResource(R.string.home_feed_flag_recommended)
    } else {
        item.subtitle?.toTravelStatusLabelOrNull() ?: stringResource(R.string.home_feed_flag_saved)
    }
    val flagBackground = when {
        isRecommended -> Color(0xF0FFFFFF)
        status == TravelStatus.COMPLETED -> Color(0xEB2F6F4F)
        else -> Color(0xEB5B7488)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(LbColors.Paper)
            .border(1.dp, if (isRecommended) LbColors.GreenLine else LbColors.Line, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .background(Brush.linearGradient(listOf(LbColors.GreenTint2, LbColors.GreenBlock)))
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    imageLoader = imageLoader,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                    contentDescription = null,
                    tint = LbColors.Green,
                    modifier = Modifier.align(Alignment.Center).size(38.dp),
                )
            }
            Text(
                text = flagLabel,
                color = if (isRecommended) LbColors.GreenForest else Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(flagBackground)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.padding(start = 15.dp, top = 13.dp, end = 15.dp, bottom = 15.dp),
        ) {
            Text(
                text = item.title,
                color = LbColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HomePopularCourseCard(course: PopularCourse, imageLoader: ImageLoader, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(LbColors.Paper)
            .border(1.dp, LbColors.Line, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = course.imageUrl,
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(LbColors.GreenTint2),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.padding(start = 13.dp, top = 11.dp, end = 13.dp, bottom = 13.dp),
        ) {
            Text(
                text = course.title,
                color = LbColors.Ink,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
