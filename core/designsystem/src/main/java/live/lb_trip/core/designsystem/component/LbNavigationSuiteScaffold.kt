package live.lb_trip.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
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
    content: @Composable () -> Unit,
) {
    val itemColors = LbNavigationSuiteItemColors
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach { item ->
                item(
                    selected = item.selected,
                    onClick = item.onClick,
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = { Text(text = item.label, fontSize = 11.sp) },
                    colors = itemColors,
                )
            }
        },
        modifier = modifier,
        containerColor = Color.White,
        content = content,
    )
}

private val LbNavigationSuiteItemColors
    @Composable get() = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = LbColors.Green,
            selectedTextColor = LbColors.Green,
            unselectedIconColor = LbColors.TabInactive,
            unselectedTextColor = LbColors.TabInactive,
            indicatorColor = Color.Transparent,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = LbColors.Green,
            selectedTextColor = LbColors.Green,
            unselectedIconColor = LbColors.TabInactive,
            unselectedTextColor = LbColors.TabInactive,
            indicatorColor = Color.Transparent,
        ),
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
