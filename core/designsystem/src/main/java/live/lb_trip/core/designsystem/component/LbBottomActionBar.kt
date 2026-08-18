package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object LbBottomActionBarDefaults {
    val ButtonHeight = 54.dp
    val ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    val ButtonSpacing = 10.dp
    val windowInsets: WindowInsets
        @Composable get() = WindowInsets.navigationBars
}

/**
 * Pinned bottom action-bar chrome shared by every screen's primary CTA: fade-to-white
 * background, nav-bar/ime inset padding, and consistent outer padding. Place a single
 * [LbBottomActionButton] inside, or an [LbBottomActionButtonRow] for multi-button bars;
 * any extra content (links, helper text) can sit alongside it in the same column.
 */
@Composable
fun LbBottomActionBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = LbBottomActionBarDefaults.windowInsets,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LbBrush.BottomFadeGradient)
            .windowInsetsPadding(windowInsets)
            .padding(LbBottomActionBarDefaults.ContentPadding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

/** A row of evenly-spaced buttons for bars with more than one action (e.g. resend + confirm). */
@Composable
fun LbBottomActionButtonRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LbBottomActionBarDefaults.ButtonSpacing),
        content = content,
    )
}

/** A single button sized/styled to match every other bottom action bar's primary CTA. */
@Composable
fun LbBottomActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = LbButtonDefaults.greenColors(),
    elevation: ButtonElevation? = LbButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    LbButton(
        onClick = {
            focusManager.clearFocus()
            onClick()
        },
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        border = border,
        modifier = modifier.heightIn(min = LbBottomActionBarDefaults.ButtonHeight),
    ) {
        leadingIcon?.invoke()
        Text(
            text = text,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        trailingIcon?.invoke()
    }
}
