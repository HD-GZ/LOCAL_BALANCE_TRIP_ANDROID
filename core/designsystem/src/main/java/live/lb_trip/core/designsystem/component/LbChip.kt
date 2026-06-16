package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.R

private val ChipBackground = Color(0xFFF0EDE6)
private val ChipBorder = Color(0xFFEBE7DF)
private val ChipDot = Color(0xFF2F6F4F)
private val ChipText = Color(0xFF5F5B53)

private val ChipGlassBackground = Color(0x21FFFFFF)
private val ChipGlassBorder = Color(0x33FFFFFF)
private val ChipGlassText = Color(0xFFDFF0E6)

@Immutable
data class LbChipColors(
    val background: Color,
    val border: Color,
    val text: Color,
)

@Immutable
data class LbChipSizes(
    val height: Dp,
    val horizontalPadding: Dp,
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
)

object LbChipDefaults {
    @Composable
    fun colors(): LbChipColors = LbChipColors(
        background = ChipBackground,
        border = ChipBorder,
        text = ChipText,
    )

    @Composable
    fun glassColors(): LbChipColors = LbChipColors(
        background = ChipGlassBackground,
        border = ChipGlassBorder,
        text = ChipGlassText,
    )

    fun sizes(): LbChipSizes = LbChipSizes(
        height = 32.dp,
        horizontalPadding = 12.dp,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    fun glassSizes(): LbChipSizes = LbChipSizes(
        height = 32.dp,
        horizontalPadding = 12.dp,
        fontSize = 10.sp,
        lineHeight = 16.sp,
    )

    @Composable
    fun Dot(color: Color = ChipDot) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(3.5.dp))
                .background(color),
        )
    }
}

@Composable
fun LbChip(
    label: String,
    modifier: Modifier = Modifier,
    colors: LbChipColors = LbChipDefaults.colors(),
    sizes: LbChipSizes = LbChipDefaults.sizes(),
    leading: @Composable (() -> Unit)? = { LbChipDefaults.Dot() },
) {
    Row(
        modifier = modifier
            .height(sizes.height)
            .clip(CircleShape)
            .background(colors.background)
            .border(width = 1.dp, color = colors.border, shape = CircleShape)
            .padding(horizontal = sizes.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        leading?.invoke()
        Text(
            text = label,
            color = colors.text,
            fontSize = sizes.fontSize,
            fontWeight = FontWeight.Normal,
            lineHeight = sizes.lineHeight,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LbChipPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        LbChip(label = "KTX 할인")
        LbChip(label = "반값여행")
        LbChip(label = "관광주민증")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF244F3A)
@Composable
private fun LbChipGlassPreview() {
    Row(modifier = Modifier.padding(16.dp)) {
        LbChip(
            label = "취향 · 가치소비 기반 AI 큐레이션",
            colors = LbChipDefaults.glassColors(),
            sizes = LbChipDefaults.glassSizes(),
            leading = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_chip_flash_leading),
                    contentDescription = "취향 · 가치소비 기반 AI 큐레이션",
                    tint = Color(0xFFDFF0E6)
                )
            },
        )
    }
}
