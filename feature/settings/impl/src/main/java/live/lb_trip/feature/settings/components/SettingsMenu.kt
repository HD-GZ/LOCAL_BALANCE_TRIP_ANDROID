package live.lb_trip.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.feature.settings.R

@Composable
fun SettingsSavedCoursesRow(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_stat_saved_courses),
            color = LbColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stringResource(R.string.settings_stat_count_template, count),
            color = LbColors.GreenDk,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = LbColors.Ink3,
            modifier = Modifier.padding(start = 6.dp).size(16.dp),
        )
    }
}

data class SettingsMenuItem(val label: String, val onClick: () -> Unit)

@Composable
fun SettingsMenuGroup(
    label: String,
    items: List<SettingsMenuItem>,
    versionName: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = LbColors.Ink3,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp)),
        ) {
            items.forEachIndexed { index, item ->
                SettingsMenuRow(label = item.label, onClick = item.onClick)
                if (index != items.lastIndex) HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
            }
            HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
            SettingsVersionRow(versionName = versionName)
        }
    }
}

@Composable
private fun SettingsMenuRow(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(
            text = label,
            color = LbColors.Ink,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = LbColors.Ink3,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun SettingsVersionRow(versionName: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_menu_version),
            color = LbColors.Ink,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = versionName,
            color = LbColors.Ink3,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun SettingsLogoutGroup(onLogoutClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp))
            .clickable(onClick = onLogoutClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_logout),
            color = LbColors.DangerStrong,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
            contentDescription = null,
            tint = LbColors.DangerStrong,
            modifier = Modifier.size(16.dp),
        )
    }
}
