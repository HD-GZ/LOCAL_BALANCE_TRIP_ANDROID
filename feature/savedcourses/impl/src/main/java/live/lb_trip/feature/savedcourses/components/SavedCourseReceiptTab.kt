package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.feature.savedcourses.SavedCourseDetailUiState

@Composable
internal fun SavedCourseReceiptTab(state: SavedCourseDetailUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        if (state.isReceiptsLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
            return@Column
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LbColors.GreenTint2, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            Text(
                text = stringResource(R.string.savedcourses_detail_receipt_total_label),
                color = LbColors.Ink,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.savedcourses_detail_receipt_amount_template, state.receiptTotalAmount),
                color = LbColors.GreenDk,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (state.receipts.isEmpty()) {
            Text(
                text = stringResource(R.string.savedcourses_detail_receipt_empty),
                color = LbColors.Ink3,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 20.dp),
            )
        } else {
            Column {
                state.receipts.fastForEachIndexed { index, receipt ->
                    key(receipt.receiptId) {
                        if (index > 0) HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
                        SavedCourseReceiptRow(
                            receipt = receipt,
                            amountLabel = stringResource(R.string.savedcourses_detail_receipt_amount_template, receipt.amount),
                        )
                    }
                }
            }
        }
    }
}
