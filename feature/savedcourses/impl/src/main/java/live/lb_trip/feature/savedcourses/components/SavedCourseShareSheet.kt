package live.lb_trip.feature.savedcourses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.feature.savedcourses.R
import live.lb_trip.core.designsystem.R as DesignSystemR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SavedCourseShareSheet(
    onDismiss: () -> Unit,
    onSaveImageClick: () -> Unit,
    onShareClick: () -> Unit,
    onKakaoShareClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.savedcourses_detail_share_sheet_title),
                color = LbColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                ShareChannelItem(
                    modifier = Modifier.weight(1f),
                    icon = ImageVector.vectorResource(DesignSystemR.drawable.ic_download),
                    label = stringResource(R.string.savedcourses_detail_share_save_image),
                    onClick = onSaveImageClick,
                )
                ShareChannelItem(
                    modifier = Modifier.weight(1f),
                    icon = ImageVector.vectorResource(DesignSystemR.drawable.ic_kakaotalk),
                    label = stringResource(R.string.savedcourses_detail_share_kakaotalk),
                    onClick = onKakaoShareClick,
                )
                ShareChannelItem(
                    modifier = Modifier.weight(1f),
                    icon = ImageVector.vectorResource(DesignSystemR.drawable.ic_share),
                    label = stringResource(R.string.savedcourses_detail_share_send),
                    onClick = onShareClick,
                )
            }
        }
    }
}

@Composable
private fun ShareChannelItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LbColors.Ink2,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            color = LbColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun ShareChannelItemPreview() {
    ShareChannelItem(
        icon = Icons.Filled.Share,
        label = "공유하기",
        onClick = {}
    )
}
