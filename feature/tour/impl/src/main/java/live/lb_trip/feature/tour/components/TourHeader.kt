package live.lb_trip.feature.tour.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.tour.R

@Composable
internal fun TourHeader(
    regionName: String,
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = LbColors.Ink3, fontSize = 9.sp, fontWeight = FontWeight.Bold)) {
            append(regionName)
        }
        append("\n")
        withStyle(SpanStyle(color = LbColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)) {
            append(title)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 16.dp),
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_back),
                    contentDescription = stringResource(R.string.tour_back_content_description),
                    tint = Color.Unspecified,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = text)
            }
        }
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
    }
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
