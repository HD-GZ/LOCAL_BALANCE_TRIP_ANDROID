package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors

/** Six-digit verification code entry, split into two groups of three boxes. */
@Composable
fun LbOtpField(
    code: String,
    onCodeChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = code,
        onValueChange = { new ->
            if (new.length <= 6 && new.all { it.isDigit() }) onCodeChange(new)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier,
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 0..2) {
                    LbOtpBox(char = code.getOrNull(i), isCurrent = code.length == i)
                    if (i < 2) Spacer(modifier = Modifier.width(9.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                for (i in 3..5) {
                    LbOtpBox(char = code.getOrNull(i), isCurrent = code.length == i)
                    if (i < 5) Spacer(modifier = Modifier.width(9.dp))
                }
            }
        },
    )
}

@Composable
private fun LbOtpBox(
    char: Char?,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 46.dp, height = 58.dp)
            .border(
                width = 1.dp,
                color = if (isCurrent) LbColors.Green else LbColors.Line,
                shape = RoundedCornerShape(13.dp),
            )
            .clip(RoundedCornerShape(13.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        if (char != null) {
            Text(
                text = char.toString(),
                color = LbColors.Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
