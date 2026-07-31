package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.feature.savedcourses.SavedCourseReceipt

@Composable
internal fun SavedCourseReceiptRow(receipt: SavedCourseReceipt, amountLabel: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = receipt.merchantName, color = Ink, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
            Text(text = receipt.paidDate, color = Ink3, fontSize = 11.sp)
        }
        Text(text = amountLabel, color = Green, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
    }
}
