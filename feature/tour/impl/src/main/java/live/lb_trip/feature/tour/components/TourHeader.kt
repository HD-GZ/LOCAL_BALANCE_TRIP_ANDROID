package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.feature.tour.R

@Composable
internal fun TourHeader(
    regionName: String,
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LbTopBar(
        onBackClick = onBackClick,
        backContentDescription = stringResource(R.string.tour_back_content_description),
        title = title,
        subtitle = regionName.ifEmpty { null },
        modifier = modifier,
    )
}

@Composable
internal fun TourProgressChip(completedCount: Int, totalCount: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.tour_progress_template, completedCount, totalCount),
        color = LbColors.GreenDk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(LbColors.Paper)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}
