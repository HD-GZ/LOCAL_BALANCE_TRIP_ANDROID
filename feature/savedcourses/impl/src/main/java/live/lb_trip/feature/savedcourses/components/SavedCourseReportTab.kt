package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseDetailUiState

private val REPORT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

@Composable
internal fun rememberReportDistanceValueLabel(distanceMeters: Float?): String? = distanceMeters?.let {
    stringResource(R.string.savedcourses_detail_report_distance_template, it.roundToInt())
}

@Composable
internal fun rememberReportMetaLabel(tourEndedAt: String): String {
    val formattedDate = runCatching { LocalDateTime.parse(tourEndedAt).format(REPORT_DATE_FORMATTER) }
        .getOrDefault(tourEndedAt)
    return stringResource(R.string.savedcourses_detail_report_meta_template, formattedDate)
}

@Composable
internal fun SavedCourseReportTab(
    state: SavedCourseDetailUiState,
    imageLoader: ImageLoader,
    metaLabel: String,
    placesLabel: String,
    amountLabel: String,
    distanceValueLabel: String?,
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

        if (state.reportImageUrl != null) {
            AsyncImage(
                model = state.reportImageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, LbColors.LineSoft), RoundedCornerShape(14.dp)),
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        Text(text = metaLabel, color = LbColors.Ink3, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))

        if (distanceValueLabel != null) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = distanceValueLabel,
                    color = LbColors.Ink,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.savedcourses_detail_report_distance_unit),
                    color = LbColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 3.dp, bottom = 6.dp),
                )
            }
            Text(
                text = stringResource(R.string.savedcourses_detail_report_distance_label),
                color = LbColors.Ink2,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 26.dp),
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SavedCourseReportSubStat(
                value = placesLabel,
                label = stringResource(R.string.savedcourses_detail_report_places_label),
                modifier = Modifier.weight(1f),
            )
            SavedCourseReportSubStat(
                value = amountLabel,
                label = stringResource(R.string.savedcourses_detail_report_amount_label),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SavedCourseReportSubStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = value, color = LbColors.Ink, fontSize = 16.5.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = LbColors.Ink2, fontSize = 11.5.sp, modifier = Modifier.padding(top = 4.dp))
    }
}
