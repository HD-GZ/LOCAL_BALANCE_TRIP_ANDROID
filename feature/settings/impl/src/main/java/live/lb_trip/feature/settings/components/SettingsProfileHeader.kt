package live.lb_trip.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import live.lb_trip.feature.settings.R

@Composable
fun SettingsProfileHeader(
    name: String,
    email: String,
    onEditInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LbColors.Paper)
            .border(1.dp, LbColors.Line, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(LbColors.GreenTint)
                .border(1.dp, LbColors.GreenLine, CircleShape),
        ) {
            Text(
                text = name.firstOrNull()?.toString().orEmpty(),
                color = LbColors.Green,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = LbColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = email,
                color = LbColors.Ink3,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onEditInfoClick)
                .padding(6.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_edit_info),
                color = LbColors.Green,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                contentDescription = null,
                tint = LbColors.Green,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
fun SettingsGuestProfileHeader(onLoginClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LbColors.Paper)
            .border(1.dp, LbColors.Line, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(LbColors.SurfaceSoft)
                .border(1.dp, LbColors.Line, CircleShape),
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = LbColors.Ink3,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_guest_title),
                color = LbColors.Ink,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.settings_guest_description),
                color = LbColors.Ink3,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(32.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(LbColors.Green)
                .clickable(onClick = onLoginClick)
                .padding(horizontal = 13.dp),
        ) {
            Text(
                text = stringResource(R.string.settings_guest_login_cta),
                color = LbColors.Paper,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
