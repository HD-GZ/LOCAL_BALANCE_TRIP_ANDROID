package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.savedcourses.R

@Composable
internal fun SavedCourseShareCard(
    regionName: String,
    title: String,
    statusLabel: String,
    placesLabel: String,
    amountLabel: String,
    dateLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LbColors.Paper)
            .border(width = 1.dp, color = LbColors.LineSoft, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        if (regionName.isNotEmpty()) {
            Text(text = regionName, color = LbColors.Ink3, fontSize = 11.sp)
        }
        Text(text = title, color = LbColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LbColors.GreenTint2),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.size(44.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShareCardStat(label = stringResource(R.string.savedcourses_detail_report_status_label), value = statusLabel)
            ShareCardStat(label = stringResource(R.string.savedcourses_detail_report_places_label), value = placesLabel)
            ShareCardStat(label = stringResource(R.string.savedcourses_detail_report_amount_label), value = amountLabel)
        }

        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp, modifier = Modifier.padding(vertical = 14.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_balance_mark),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = stringResource(R.string.savedcourses_detail_share_card_brand),
                color = LbColors.Ink3,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.dp).weight(1f),
            )
            Text(text = dateLabel, color = LbColors.Ink4, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ShareCardStat(label: String, value: String) {
    Column {
        Text(text = label, color = LbColors.Ink3, fontSize = 10.sp)
        Text(text = value, color = LbColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
