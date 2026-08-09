package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseDetailUiState

private const val METERS_PER_KILOMETER = 1000f

internal data class ReportMovementLabels(val distance: String?, val steps: String?)

@Composable
internal fun rememberReportMovementLabels(distanceMeters: Float?, stepCount: Int?): ReportMovementLabels {
    val distanceLabel = distanceMeters?.let {
        stringResource(R.string.savedcourses_detail_report_distance_template, it / METERS_PER_KILOMETER)
    }
    val stepsLabel = stepCount?.let {
        stringResource(R.string.savedcourses_detail_report_steps_template, it)
    }
    return ReportMovementLabels(distance = distanceLabel, steps = stepsLabel)
}

@Composable
internal fun SavedCourseReportTab(
    state: SavedCourseDetailUiState,
    imageLoader: ImageLoader,
    statusLabel: String,
    placesLabel: String,
    amountLabel: String,
    distanceLabel: String?,
    stepsLabel: String?,
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
        if (distanceLabel != null) {
            HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
            SavedCourseReportRow(label = stringResource(R.string.savedcourses_detail_report_distance_label), value = distanceLabel)
        }
        if (stepsLabel != null) {
            HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
            SavedCourseReportRow(label = stringResource(R.string.savedcourses_detail_report_steps_label), value = stepsLabel)
        }

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
