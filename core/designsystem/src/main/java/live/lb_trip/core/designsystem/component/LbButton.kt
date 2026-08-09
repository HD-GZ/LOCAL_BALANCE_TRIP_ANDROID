package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import live.lb_trip.core.designsystem.LbColors

object LbButtonDefaults {
    @Composable
    fun whiteColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = LbColors.Paper,
            contentColor = LbColors.Ink,
        )
    }

    @Composable
    fun greenColors(): ButtonColors {
        return ButtonDefaults.buttonColors(
            containerColor = LbColors.Green,
            contentColor = LbColors.Paper,
        )
    }

    @Composable
    fun buttonElevation(): ButtonElevation {
        return ButtonDefaults.buttonElevation()
    }

    val shape: Shape
        @Composable get() = RoundedCornerShape(12.dp)
}

@Composable
fun LbButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = LbButtonDefaults.shape,
    colors: ButtonColors = LbButtonDefaults.greenColors(),
    elevation: ButtonElevation? = LbButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}
