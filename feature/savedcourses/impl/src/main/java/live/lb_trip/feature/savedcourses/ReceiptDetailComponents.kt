package live.lb_trip.feature.savedcourses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbBottomActionButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbInputField

@Composable
internal fun ReceiptImage(imageUrl: String?, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        imageLoader = imageLoader,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LbColors.SurfaceSoft)
            .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp)),
    )
}

@Composable
internal fun ReceiptInfoContent(state: ReceiptDetailUiState, onIntent: (ReceiptDetailIntent) -> Unit, modifier: Modifier = Modifier) {
    val isEditSubmittable = state.editMerchantName.isNotEmpty() && state.editAmount.isNotEmpty() && state.editPaidDate.isNotEmpty()
    val amountFocusRequester = remember { FocusRequester() }
    val paidDateFocusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        if (state.isEditing) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 18.dp),
            ) {
                LbInputField(
                    value = state.editMerchantName,
                    onValueChange = { onIntent(ReceiptDetailIntent.MerchantNameChanged(it)) },
                    label = stringResource(R.string.savedcourses_receipt_detail_field_merchant),
                    placeholder = stringResource(R.string.savedcourses_receipt_detail_field_merchant),
                    required = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { amountFocusRequester.requestFocus() }),
                )
                LbInputField(
                    value = state.editAmount,
                    onValueChange = { onIntent(ReceiptDetailIntent.AmountChanged(it)) },
                    label = stringResource(R.string.savedcourses_receipt_detail_field_amount),
                    placeholder = "0",
                    required = true,
                    visualTransformation = ThousandsSeparatorVisualTransformation,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { paidDateFocusRequester.requestFocus() }),
                    textFieldModifier = Modifier.focusRequester(amountFocusRequester),
                )
                LbInputField(
                    value = state.editPaidDate,
                    onValueChange = { onIntent(ReceiptDetailIntent.PaidDateChanged(it)) },
                    label = stringResource(R.string.savedcourses_receipt_detail_field_date),
                    placeholder = "YYYY-MM-DD",
                    required = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isEditSubmittable && !state.isSaving) onIntent(ReceiptDetailIntent.SaveClicked)
                        },
                    ),
                    textFieldModifier = Modifier.focusRequester(paidDateFocusRequester),
                )
            }
            Text(
                text = stringResource(R.string.savedcourses_receipt_detail_edit_hint),
                color = LbColors.Ink2,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(top = 10.dp, bottom = 18.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                LbBottomActionButton(
                    text = stringResource(R.string.savedcourses_receipt_detail_save),
                    onClick = { onIntent(ReceiptDetailIntent.SaveClicked) },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
                LbBottomActionButton(
                    text = stringResource(R.string.savedcourses_receipt_detail_cancel),
                    onClick = { onIntent(ReceiptDetailIntent.EditCancelled) },
                    enabled = !state.isSaving,
                    colors = LbButtonDefaults.whiteColors(),
                    border = BorderStroke(1.dp, LbColors.Line2),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(top = 18.dp, bottom = 18.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(LbColors.Paper)
                    .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp)),
            ) {
                ReceiptDetailRow(
                    label = stringResource(R.string.savedcourses_receipt_detail_field_merchant),
                    value = state.merchantName,
                )
                HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                ReceiptDetailRow(
                    label = stringResource(R.string.savedcourses_receipt_detail_field_amount),
                    value = stringResource(R.string.savedcourses_detail_receipt_amount_template, state.amount.toIntOrNull() ?: 0),
                )
                HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                ReceiptDetailRow(
                    label = stringResource(R.string.savedcourses_receipt_detail_field_date),
                    value = state.paidDate,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                LbBottomActionButton(
                    text = stringResource(R.string.savedcourses_receipt_detail_download),
                    onClick = { onIntent(ReceiptDetailIntent.DownloadClicked) },
                    colors = LbButtonDefaults.whiteColors(),
                    border = BorderStroke(1.dp, LbColors.Line2),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_download),
                            contentDescription = null,
                            tint = LbColors.Ink,
                            modifier = Modifier.size(17.dp).padding(end = 8.dp),
                        )
                    },
                )
                LbBottomActionButton(
                    text = stringResource(R.string.savedcourses_receipt_detail_delete),
                    onClick = { onIntent(ReceiptDetailIntent.DeleteClicked) },
                    colors = receiptDetailDangerColors(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
internal fun receiptDetailDangerColors() = ButtonDefaults.buttonColors(
    containerColor = LbColors.DangerStrong.copy(alpha = 0.08f),
    contentColor = LbColors.DangerStrong,
)

/** Displays a digit-only amount with thousands separators (e.g. "13820" -> "13,820") without altering the underlying value. */
internal object ThousandsSeparatorVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = StringBuilder()
        val transformedOffsetAt = IntArray(digits.length + 1)
        digits.forEachIndexed { index, digit ->
            if (index != 0 && (digits.length - index) % 3 == 0) formatted.append(',')
            transformedOffsetAt[index] = formatted.length
            formatted.append(digit)
        }
        transformedOffsetAt[digits.length] = formatted.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                transformedOffsetAt[offset.coerceIn(0, digits.length)]

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                val index = transformedOffsetAt.indexOfFirst { it >= clamped }
                return if (index == -1) digits.length else index
            }
        }

        return TransformedText(AnnotatedString(formatted.toString()), offsetMapping)
    }
}

@Composable
private fun ReceiptDetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp),
    ) {
        Text(
            text = label,
            color = LbColors.Ink3,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(100.dp),
        )
        Text(text = value, color = LbColors.Ink, fontSize = 13.sp, modifier = Modifier.weight(1f))
    }
}
