package live.lb_trip.core.util

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val STICKER_WIDTH = 512

fun drawInstagramSticker(
    courseTitle: String,
    distanceValue: String,
    distanceLabel: String,
    dateValue: String,
    dateLabel: String,
    appName: String,
    textMeasurer: TextMeasurer,
    width: Int = STICKER_WIDTH,
): ImageBitmap {
    val topPadding = width * 0.06f
    val titleToStat = width * 0.05f
    val statToLabel = width * 0.015f
    val labelToNextStat = width * 0.04f
    val middlePadding = width * 0.02f
    val bottomPadding = width * 0.06f

    val titleLayout = textMeasurer.measure(courseTitle, stickerTextStyle(width * 0.02f, FontWeight.Bold))
    val distanceValueLayout = textMeasurer.measure(distanceValue, stickerTextStyle(width * 0.05f, FontWeight.Bold))
    val distanceUnitLayout = textMeasurer.measure("m", stickerTextStyle(width * 0.035f, FontWeight.SemiBold))
    val distanceLabelLayout =
        textMeasurer.measure(distanceLabel, stickerTextStyle(width * 0.015f, FontWeight.Normal, alpha = 0.85f))
    val dateValueLayout = textMeasurer.measure(dateValue, stickerTextStyle(width * 0.05f, FontWeight.Bold))
    val dateLabelLayout =
        textMeasurer.measure(dateLabel, stickerTextStyle(width * 0.015f, FontWeight.Normal, alpha = 0.85f))
    val appNameLayout =
        textMeasurer.measure(appName, stickerTextStyle(width * 0.01f, FontWeight.Normal, alpha = 0.85f))

    val distanceStatHeight = maxOf(distanceValueLayout.size.height, distanceUnitLayout.size.height)

    val height = (
        topPadding +
            titleLayout.size.height + titleToStat +
            distanceStatHeight + statToLabel +
            distanceLabelLayout.size.height + labelToNextStat +
            dateValueLayout.size.height + statToLabel +
            dateLabelLayout.size.height +
            appNameLayout.size.height +
            middlePadding +
            bottomPadding
        ).toInt()

    val imageBitmap = ImageBitmap(width = width, height = height)
    val canvas = Canvas(imageBitmap)
    val drawScope = CanvasDrawScope()

    drawScope.draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat()),
    ) {
        drawRoundRect(
            color = Color(0x66222222),
            size = size,
            cornerRadius = CornerRadius(x = 32.dp.toPx(), y = 32.dp.toPx())
        )

        var y = topPadding

        drawCentered(titleLayout, width, y)
        y += titleLayout.size.height + titleToStat

        drawCenteredStatRow(distanceValueLayout, distanceUnitLayout, width, y)
        y += distanceStatHeight + statToLabel

        drawCentered(distanceLabelLayout, width, y)
        y += distanceLabelLayout.size.height + labelToNextStat

        drawCentered(dateValueLayout, width, y)
        y += dateValueLayout.size.height + statToLabel

        drawCentered(dateLabelLayout, width, y)
        y += dateLabelLayout.size.height + statToLabel + middlePadding

        drawCentered(appNameLayout, width, y)
    }

    return imageBitmap
}

private fun stickerTextStyle(fontSizePx: Float, fontWeight: FontWeight, alpha: Float = 1f): TextStyle = TextStyle(
    color = Color.White.copy(alpha = alpha),
    fontSize = fontSizePx.sp,
    fontWeight = fontWeight,
)

private fun DrawScope.drawCentered(layout: TextLayoutResult, canvasWidth: Int, top: Float) {
    val left = (canvasWidth - layout.size.width) / 2f
    drawText(textLayoutResult = layout, topLeft = Offset(left, top))
}

private fun DrawScope.drawCenteredStatRow(
    valueLayout: TextLayoutResult,
    unitLayout: TextLayoutResult,
    canvasWidth: Int,
    top: Float,
) {
    val spacing = canvasWidth * 0.012f
    val totalWidth = valueLayout.size.width + spacing + unitLayout.size.width

    var left = (canvasWidth - totalWidth) / 2f
    drawText(textLayoutResult = valueLayout, topLeft = Offset(left, top))
    left += valueLayout.size.width + spacing

    val unitTop = top + valueLayout.size.height - unitLayout.size.height
    drawText(textLayoutResult = unitLayout, topLeft = Offset(left, unitTop))
}
