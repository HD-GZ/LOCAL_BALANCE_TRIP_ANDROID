package live.lb_trip.feature.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.slack.circuit.runtime.ui.Ui
import live.lb_trip.core.designsystem.R
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults

private val TextPrimary = Color(0xFF222019)
private val Brand = Color(0xFF2F6F4F)
private val Border = Color(0xFFEBE7DF)
private val BodyBackground = Color(0xFFF3F1EC)
private val TabInactive = Color(0xFF9A958C)

class HomeUi : Ui<HomeState> {
    @Composable
    override fun Content(state: HomeState, modifier: Modifier) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            }
        }

        HomeScreenContent(
            onStartDiagnosisClick = { state.eventSink(HomeEvent.StartPropensityDiagnosis) },
            onMyInfoClick = { state.eventSink(HomeEvent.NavigateToSettings) },
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeScreenContent(
    onStartDiagnosisClick: () -> Unit,
    onMyInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeBrandBar()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(BodyBackground),
            contentAlignment = Alignment.Center,
        ) {
            LbButton(
                onClick = onStartDiagnosisClick,
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.height(56.dp),
            ) {
                Text(text = "취향 진단 시작하기", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        HomeBottomTabs(onMyInfoClick = onMyInfoClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeBrandBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_balance_mark),
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("로컬")
                            withStyle(SpanStyle(color = Brand)) { append("밸런스") }
                            append(" 트립")
                        },
                        color = TextPrimary,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        )
        HorizontalDivider(color = Border, thickness = 1.dp)
    }
}

@Composable
private fun HomeBottomTabs(onMyInfoClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = Border, thickness = 1.dp)
        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "메인") },
                label = { Text(text = "메인", fontSize = 11.sp) },
                colors = HomeNavigationBarItemColors,
            )
            NavigationBarItem(
                selected = false,
                onClick = onMyInfoClick,
                icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = "나의 정보") },
                label = { Text(text = "나의 정보", fontSize = 11.sp) },
                colors = HomeNavigationBarItemColors,
            )
        }
    }
}

private val HomeNavigationBarItemColors
    @Composable get() = NavigationBarItemDefaults.colors(
        selectedIconColor = Brand,
        selectedTextColor = Brand,
        unselectedIconColor = TabInactive,
        unselectedTextColor = TabInactive,
        indicatorColor = Color.Transparent,
    )

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreenContent(onStartDiagnosisClick = {}, onMyInfoClick = {})
}
