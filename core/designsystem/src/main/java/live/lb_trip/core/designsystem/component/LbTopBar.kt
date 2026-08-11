package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R

private val TightLineHeightTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both,
    ),
)

private val CompactBarHeight = 44.dp

/**
 * Shrinks to [CompactBarHeight] automatically on compact-height windows (e.g. phone landscape) and
 * on medium-or-wider windows (tablet/unfolded, where a nav rail replaces the bottom bar) — in both
 * cases a full-height Material [TopAppBar] would eat a disproportionate share of the screen. Only
 * plain phone-portrait windows keep the standard taller bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LbTopBar(
    onBackClick: () -> Unit,
    backContentDescription: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    titleTrailing: (@Composable () -> Unit)? = null,
    containerColor: Color = LbColors.Paper,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    actions: @Composable RowScope.() -> Unit = {},
    showBackButton: Boolean = true,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isHeightCompact = !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    val isWidthMediumOrWider = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val useCompactBar = isHeightCompact || isWidthMediumOrWider

    val titleContent: @Composable () -> Unit = {
        if (title != null) {
            val tightStyle = LocalTextStyle.current.merge(TightLineHeightTextStyle)
            val titleText = @Composable {
                Text(
                    text = title,
                    color = LbColors.Ink,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp,
                    style = tightStyle,
                )
            }
            if (subtitle != null) {
                Column {
                    Text(
                        text = subtitle,
                        color = LbColors.Ink3,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        style = tightStyle,
                    )
                    titleText()
                }
            } else if (titleTrailing != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    titleText()
                    titleTrailing()
                }
            } else {
                titleText()
            }
        }
    }

    if (useCompactBar) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(containerColor)
                .windowInsetsPadding(windowInsets)
                .heightIn(min = CompactBarHeight)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                        contentDescription = backContentDescription,
                        tint = Color.Unspecified,
                    )
                }
            } else {
                Row(modifier = Modifier.padding(start = 12.dp)) {}
            }
            titleContent()
            Row(modifier = Modifier.weight(1f)) {}
            actions()
        }
    } else {
        TopAppBar(
            title = titleContent,
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                            contentDescription = backContentDescription,
                            tint = Color.Unspecified,
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
            windowInsets = windowInsets,
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LbTopBarPreview() {
    LbTopBar(
        onBackClick = {},
        backContentDescription = "뒤로",
        title = "저장한 코스",
        subtitle = "강원 정선군",
    )
}
