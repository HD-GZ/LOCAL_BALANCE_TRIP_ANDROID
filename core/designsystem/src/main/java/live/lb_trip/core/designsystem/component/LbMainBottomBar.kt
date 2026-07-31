package live.lb_trip.core.designsystem.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors

@Composable
fun LbMainBottomBar(items: List<LbBottomTabItem>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
        NavigationBar(containerColor = Color.White) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = item.selected,
                    onClick = item.onClick,
                    icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                    label = { Text(text = item.label, fontSize = 11.sp) },
                    colors = LbBottomTabItemColors,
                )
            }
        }
    }
}

private val LbBottomTabItemColors
    @Composable get() = NavigationBarItemDefaults.colors(
        selectedIconColor = LbColors.Green,
        selectedTextColor = LbColors.Green,
        unselectedIconColor = LbColors.TabInactive,
        unselectedTextColor = LbColors.TabInactive,
        indicatorColor = Color.Transparent,
    )

@Preview(showBackground = true)
@Composable
private fun LbMainBottomBarPreview() {
    LbMainBottomBar(
        items = listOf(
            LbBottomTabItem(label = "메인", icon = Icons.Filled.Home, selected = true, onClick = {}),
            LbBottomTabItem(label = "나의 정보", icon = Icons.Outlined.Person, selected = false, onClick = {}),
        ),
    )
}
