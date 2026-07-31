package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.feature.savedcourses.SavedCourseDetailTab

@Composable
internal fun SavedCourseDetailTabBar(
    selectedTab: SavedCourseDetailTab,
    onTabSelected: (SavedCourseDetailTab) -> Unit,
    tabLabel: @Composable (SavedCourseDetailTab) -> String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ScreenBg)
            .padding(4.dp),
    ) {
        SavedCourseDetailTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Text(
                text = tabLabel(tab),
                color = if (isSelected) Green else Ink3,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Paper else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 9.dp),
            )
        }
    }
}
