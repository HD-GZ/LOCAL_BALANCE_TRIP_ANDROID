package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import live.lb_trip.core.designsystem.component.LbBottomActionBar
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbTopBar
import live.lb_trip.domain.model.TravelStatus
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseDetailTab

@Composable
internal fun SavedCourseDetailAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
) {
    LbTopBar(
        title = title,
        onBackClick = onBackClick,
        backContentDescription = stringResource(R.string.savedcourses_detail_back_content_description),
        modifier = modifier,
        showBackButton = showBackButton,
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
    LbBottomActionBar(modifier = modifier) {
        when (tab) {
            SavedCourseDetailTab.COURSE -> LbBottomActionButton(
                text = stringResource(R.string.savedcourses_detail_cta_tour_start),
                onClick = onTourStartClick,
                enabled = hasStops && status != TravelStatus.COMPLETED,
                modifier = Modifier.fillMaxWidth(),
            )
            SavedCourseDetailTab.RECEIPT -> LbBottomActionButton(
                text = stringResource(R.string.savedcourses_detail_cta_register_receipt),
                onClick = onRegisterReceiptClick,
                modifier = Modifier.fillMaxWidth(),
            )
            SavedCourseDetailTab.REPORT -> LbBottomActionButton(
                text = stringResource(R.string.savedcourses_detail_report_share_button),
                onClick = onShareClick,
                enabled = isReportAvailable,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
