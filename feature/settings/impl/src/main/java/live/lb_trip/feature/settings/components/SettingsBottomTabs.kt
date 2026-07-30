package live.lb_trip.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.settings.R

private val TabInactive = Color(0xFF9A958C)

@Composable
fun SettingsBottomTabs(onMainClick: () -> Unit, modifier: Modifier = Modifier) {
    val mainTabLabel = stringResource(R.string.settings_tab_main)
    val myInfoTabLabel = stringResource(R.string.settings_tab_my_info)

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                selected = false,
                onClick = onMainClick,
                icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = mainTabLabel) },
                label = { Text(text = mainTabLabel, fontSize = 11.sp) },
                colors = SettingsNavigationBarItemColors,
            )
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = myInfoTabLabel) },
                label = { Text(text = myInfoTabLabel, fontSize = 11.sp) },
                colors = SettingsNavigationBarItemColors,
            )
        }
    }
}

private val SettingsNavigationBarItemColors
    @Composable get() = NavigationBarItemDefaults.colors(
        selectedIconColor = LbColors.Green,
        selectedTextColor = LbColors.Green,
        unselectedIconColor = TabInactive,
        unselectedTextColor = TabInactive,
        indicatorColor = Color.Transparent,
    )
