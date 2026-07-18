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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults

private val TextPrimary = LbColors.Ink
private val Brand = LbColors.Green
private val Border = LbColors.LineSoft
private val BodyBackground = LbColors.ScreenBg
private val TabInactive = Color(0xFF9A958C)
private val HeroGradientMid = Color(0xFF276044)
private val HeroGradientEnd = Color(0xFF245A40)
private val HeroBlob = Color(0x24FFFFFF)
private val HeroEyebrowText = Color(0xB8FFFFFF)
private val HeroBodyText = Color(0xD1FFFFFF)

@Composable
internal fun HomeScreen(
    onStartDiagnosisClick: () -> Unit,
    onMyInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        HomeBrandBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(BodyBackground)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 22.dp),
        ) {
            HomeDiagnosisHero(onStartDiagnosisClick = onStartDiagnosisClick)
        }

        HomeBottomTabs(onMyInfoClick = onMyInfoClick)
    }
}

@Composable
private fun HomeDiagnosisHero(onStartDiagnosisClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(LbColors.GreenDeep, HeroGradientMid, HeroGradientEnd)),
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-34).dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(HeroBlob, Color.Transparent))),
        )
        Column(modifier = Modifier.padding(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 20.dp)) {
            Text(
                text = stringResource(R.string.home_hero_eyebrow),
                color = HeroEyebrowText,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.06.em,
            )
            Text(
                text = stringResource(R.string.home_hero_title),
                color = Color.White,
                fontSize = 21.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.02).em,
                modifier = Modifier.padding(top = 9.dp, bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.home_hero_description),
                color = HeroBodyText,
                fontSize = 12.5.sp,
                lineHeight = 19.4.sp,
                modifier = Modifier.padding(bottom = 17.dp),
            )
            LbButton(
                onClick = onStartDiagnosisClick,
                colors = LbButtonDefaults.whiteColors(),
                shape = RoundedCornerShape(13.dp),
                elevation = null,
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                Text(
                    text = stringResource(R.string.home_cta_start_diagnosis),
                    color = LbColors.GreenForest,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = LbColors.GreenForest,
                    modifier = Modifier.padding(start = 8.dp).size(18.dp),
                )
            }
        }
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
                        imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_balance_mark),
                        contentDescription = null,
                        tint = Brand,
                        modifier = Modifier.size(26.dp),
                    )
                    val brandPrefix = stringResource(R.string.home_brand_prefix)
                    val brandHighlight = stringResource(R.string.home_brand_highlight)
                    val brandSuffix = stringResource(R.string.home_brand_suffix)
                    Text(
                        text = buildAnnotatedString {
                            append(brandPrefix)
                            withStyle(SpanStyle(color = Brand)) { append(brandHighlight) }
                            append(brandSuffix)
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
    val mainTabLabel = stringResource(R.string.home_tab_main)
    val myInfoTabLabel = stringResource(R.string.home_tab_my_info)

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = Border, thickness = 1.dp)
        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = mainTabLabel) },
                label = { Text(text = mainTabLabel, fontSize = 11.sp) },
                colors = HomeNavigationBarItemColors,
            )
            NavigationBarItem(
                selected = false,
                onClick = onMyInfoClick,
                icon = { Icon(imageVector = Icons.Outlined.Person, contentDescription = myInfoTabLabel) },
                label = { Text(text = myInfoTabLabel, fontSize = 11.sp) },
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
    HomeScreen(onStartDiagnosisClick = {}, onMyInfoClick = {})
}
