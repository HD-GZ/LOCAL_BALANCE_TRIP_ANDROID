package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.domain.model.TravelStatus
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseDetailTab

@Composable
internal fun SavedCourseDetailAppBar(
    regionName: String,
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LbTopBar(
        title = title,
        subtitle = regionName.ifEmpty { null },
        onBackClick = onBackClick,
        backContentDescription = stringResource(R.string.savedcourses_detail_back_content_description),
        modifier = modifier,
    )
}

@Composable
internal fun SavedCourseDetailCtaBar(
    tab: SavedCourseDetailTab,
    status: TravelStatus,
    hasStops: Boolean,
    isReportAvailable: Boolean,
    onTourStartClick: () -> Unit,
    onRegisterReceiptClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = LineSoft, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Paper)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 11.dp),
        ) {
            when (tab) {
                SavedCourseDetailTab.COURSE -> LbButton(
                    onClick = onTourStartClick,
                    enabled = hasStops && status != TravelStatus.COMPLETED,
                    colors = LbButtonDefaults.greenColors(),
                    modifier = Modifier.weight(1f).height(46.dp),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_detail_cta_tour_start),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                SavedCourseDetailTab.RECEIPT -> LbButton(
                    onClick = onRegisterReceiptClick,
                    colors = LbButtonDefaults.greenColors(),
                    modifier = Modifier.weight(1f).height(46.dp),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_detail_cta_register_receipt),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                SavedCourseDetailTab.REPORT -> LbButton(
                    onClick = onShareClick,
                    enabled = isReportAvailable,
                    colors = LbButtonDefaults.greenColors(),
                    modifier = Modifier.weight(1f).height(46.dp),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_detail_report_share_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
