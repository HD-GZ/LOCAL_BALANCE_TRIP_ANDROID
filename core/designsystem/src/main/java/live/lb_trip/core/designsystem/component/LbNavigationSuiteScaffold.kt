package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors

@Composable
fun LbNavigationSuiteScaffold(
    items: List<LbBottomTabItem>,
    modifier: Modifier = Modifier,
    state: NavigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState(),
    content: @Composable () -> Unit,
) {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
    val itemColors = LbNavigationSuiteItemColors

    NavigationSuiteScaffold(
        navigationItems = {
            items.forEach { item ->
                NavigationSuiteItem(
                    selected = item.selected,
                    onClick = item.onClick,
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = { Text(text = item.label, fontSize = 11.sp) },
                    navigationSuiteType = navigationSuiteType,
                    colors = itemColors,
                )
            }
        },
        modifier = modifier,
        navigationSuiteType = navigationSuiteType,
        state = state,
        containerColor = Color.White,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            shortNavigationBarContainerColor = LbColors.Paper,
            navigationBarContainerColor = LbColors.Paper,
            wideNavigationRailColors = WideNavigationRailDefaults.colors(containerColor = LbColors.Paper),
        ),
        navigationItemVerticalArrangement = Arrangement.Center,
        content = content,
    )
}

private val LbNavigationSuiteItemColors
    get() = NavigationItemColors(
        selectedIconColor = LbColors.Green,
        selectedTextColor = LbColors.Green,
        selectedIndicatorColor = LbColors.GreenTint,
        unselectedIconColor = LbColors.TabInactive,
        unselectedTextColor = LbColors.TabInactive,
        disabledIconColor = LbColors.TabInactive,
        disabledTextColor = LbColors.TabInactive,
    )

@Preview(showBackground = true)
@Composable
private fun LbNavigationSuiteScaffoldPreview() {
    LbNavigationSuiteScaffold(
        items = listOf(
            LbBottomTabItem(label = "메인", icon = Icons.Filled.Home, selected = true, onClick = {}),
            LbBottomTabItem(label = "나의 정보", icon = Icons.Outlined.Person, selected = false, onClick = {}),
        ),
        content = {},
    )
}
