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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.model.PopularCourse
import live.lb_trip.domain.model.TravelStatus

@Composable
internal fun HomeFeedSection(
    state: HomeUiState,
    onSavedAllClick: () -> Unit,
    onFeedItemClick: (HomeFeedItem) -> Unit,
    onNavigateToSignin: () -> Unit,
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
            HomeLoginPromptCard(onLoginClick = onNavigateToSignin)
            if (state.popularCourses.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 26.dp)) {
                    HomeSectionHeader(title = stringResource(R.string.home_popular_section_title))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        items(state.popularCourses, key = { it.courseId }) { course ->
                            HomePopularCourseCard(
                                course = course,
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
                HomeFeedCard(item = item, onClick = { onFeedItemClick(item) }, modifier = Modifier.width(230.dp))
            }
        }
    }
}

@Composable
private fun HomeLoginPromptCard(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LbColors.Paper)
            .border(1.dp, LbColors.Line2, RoundedCornerShape(16.dp))
            .padding(vertical = 26.dp, horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.home_feed_login_title),
            color = LbColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.home_feed_login_desc),
            color = LbColors.Ink3,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 15.dp),
        )
        LbButton(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().height(44.dp)) {
            Text(text = stringResource(R.string.home_feed_login_cta), fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
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
private fun HomeFeedCard(item: HomeFeedItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(OkHttpNetworkFetcherFactory()) }.build()
    }
    val isRecommended = item is HomeFeedItem.RecommendedRegionItem
    val flagLabel = if (isRecommended) {
        stringResource(R.string.home_feed_flag_recommended)
    } else {
        item.subtitle?.toTravelStatusLabelOrNull() ?: stringResource(R.string.home_feed_flag_saved)
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
                    .background(if (isRecommended) Color(0xF0FFFFFF) else Color(0xEB5B7488))
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
            val recommendedSubtitle = item.subtitle
            if (isRecommended && recommendedSubtitle != null) {
                Text(
                    text = recommendedSubtitle,
                    color = LbColors.Green,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun HomePopularCourseCard(course: PopularCourse, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context).components { add(OkHttpNetworkFetcherFactory()) }.build()
    }
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
                text = course.regionName,
                color = LbColors.Ink3,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
