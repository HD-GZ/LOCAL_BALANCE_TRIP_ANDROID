package live.lb_trip.feature.savedcourses

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
internal fun ReceiptStatusBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(bottom = 14.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(LbColors.GreenTint)
            .border(1.dp, LbColors.GreenLine, RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = LbColors.Green,
            modifier = Modifier.size(15.dp),
        )
        Text(
            text = stringResource(R.string.savedcourses_receipt_detail_state_registered),
            color = LbColors.GreenDk,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

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
                )
                LbInputField(
                    value = state.editAmount,
                    onValueChange = { onIntent(ReceiptDetailIntent.AmountChanged(it)) },
                    label = stringResource(R.string.savedcourses_receipt_detail_field_amount),
                    placeholder = "0",
                    required = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                LbInputField(
                    value = state.editPaidDate,
                    onValueChange = { onIntent(ReceiptDetailIntent.PaidDateChanged(it)) },
                    label = stringResource(R.string.savedcourses_receipt_detail_field_date),
                    placeholder = "YYYY-MM-DD",
                    required = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

@Composable
private fun ReceiptDetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp)) {
        Text(text = label, color = LbColors.Ink3, fontSize = 12.sp, modifier = Modifier.width(72.dp))
        Text(text = value, color = LbColors.Ink, fontSize = 13.sp, modifier = Modifier.weight(1f))
    }
}
