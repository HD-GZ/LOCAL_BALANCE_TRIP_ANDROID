package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R

private val TightLineHeightTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both,
    ),
)

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
) {
    TopAppBar(
        title = {
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
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                    contentDescription = backContentDescription,
                    tint = Color.Unspecified,
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
        windowInsets = windowInsets,
        modifier = modifier,
    )
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
