package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.savedcourses.SavedCourseBenefit

@Composable
internal fun SavedCourseBenefitRow(benefit: SavedCourseBenefit, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = benefit.title, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            if (benefit.description != null) {
                Text(text = benefit.description, color = Ink3, fontSize = 12.sp)
            }
        }
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Ink4,
            modifier = Modifier.size(16.dp),
        )
    }
}
