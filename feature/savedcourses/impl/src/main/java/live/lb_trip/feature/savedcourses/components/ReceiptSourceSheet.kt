package live.lb_trip.feature.savedcourses.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.savedcourses.R

internal fun createReceiptImageUri(context: Context): Uri {
    val receiptDir = File(context.cacheDir, "receipt_images").apply { mkdirs() }
    val file = File(receiptDir, "receipt_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.savedcourses.fileprovider", file)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReceiptSourceSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.savedcourses_receipt_source_sheet_title),
                color = LbColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 4.dp),
            )
            ReceiptSourceRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                label = stringResource(R.string.savedcourses_receipt_source_gallery),
                onClick = onGalleryClick
            )
            ReceiptSourceRow(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                label = stringResource(R.string.savedcourses_receipt_source_camera),
                onClick = onCameraClick
            )
        }
    }
}

@Composable
private fun ReceiptSourceRow(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = LbColors.Ink2, fontSize = 14.sp, fontWeight = FontWeight.Normal)
    }
}
