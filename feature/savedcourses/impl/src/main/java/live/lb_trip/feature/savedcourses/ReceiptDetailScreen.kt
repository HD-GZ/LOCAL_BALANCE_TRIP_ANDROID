package live.lb_trip.feature.savedcourses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.coroutines.launch
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbInputField

@Composable
internal fun ReceiptDetailScreen(
    onBack: () -> Unit,
    onUpdated: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory()) }
            .build()
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.savedcourses_receipt_detail_error_load)
    val updateErrorMessage = stringResource(R.string.savedcourses_receipt_detail_error_update)
    val invalidInputMessage = stringResource(R.string.savedcourses_receipt_detail_error_invalid)
    val updatedMessage = stringResource(R.string.savedcourses_receipt_detail_updated)
    val deleteErrorMessage = stringResource(R.string.savedcourses_receipt_detail_error_delete)
    val downloadSavedMessage = stringResource(R.string.savedcourses_receipt_detail_download_saved)
    val downloadFailedMessage = stringResource(R.string.savedcourses_receipt_detail_download_failed)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                ReceiptDetailSideEffect.ShowLoadError -> launch { snackbarHostState.showSnackbar(loadErrorMessage) }
                ReceiptDetailSideEffect.ShowUpdateError -> launch { snackbarHostState.showSnackbar(updateErrorMessage) }
                ReceiptDetailSideEffect.ShowInvalidInput -> launch { snackbarHostState.showSnackbar(invalidInputMessage) }
                ReceiptDetailSideEffect.ShowUpdated -> {
                    onUpdated()
                    launch { snackbarHostState.showSnackbar(updatedMessage) }
                }
                ReceiptDetailSideEffect.ShowDeleteError -> launch { snackbarHostState.showSnackbar(deleteErrorMessage) }
                is ReceiptDetailSideEffect.DownloadUrlReady -> launch {
                    val bitmap = downloadReportImageBitmap(imageLoader, context, effect.url)
                    val saved = bitmap != null && saveBitmapToGallery(context, bitmap, receiptImageFileName())
                    snackbarHostState.showSnackbar(if (saved) downloadSavedMessage else downloadFailedMessage)
                }
                ReceiptDetailSideEffect.ShowDownloadError -> launch { snackbarHostState.showSnackbar(downloadFailedMessage) }
                ReceiptDetailSideEffect.NavigateBackWithDeleted -> onDeleted()
            }
        }
    }

    ReceiptDetailScreenContent(
        state = state,
        imageLoader = imageLoader,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBack = onBack,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

private fun receiptImageFileName(): String = "lbt_receipt_${System.currentTimeMillis()}.png"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceiptDetailScreenContent(
    state: ReceiptDetailUiState,
    imageLoader: ImageLoader,
    snackbarHost: @Composable () -> Unit,
    onBack: () -> Unit,
    onIntent: (ReceiptDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ReceiptDetailTopBar(
                onBackClick = onBack,
                showEdit = !state.isLoading && !state.isEditing,
                onEditClick = { onIntent(ReceiptDetailIntent.EditClicked) },
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LbColors.Green)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
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

            AsyncImage(
                model = state.imageUrl,
                contentDescription = null,
                imageLoader = imageLoader,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp, max = 420.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LbColors.SurfaceSoft)
                    .border(1.dp, LbColors.Line, RoundedCornerShape(14.dp)),
            )

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
                    LbButton(
                        onClick = { onIntent(ReceiptDetailIntent.SaveClicked) },
                        enabled = !state.isSaving,
                        colors = LbButtonDefaults.greenColors(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.savedcourses_receipt_detail_save),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    LbButton(
                        onClick = { onIntent(ReceiptDetailIntent.EditCancelled) },
                        enabled = !state.isSaving,
                        colors = LbButtonDefaults.whiteColors(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.savedcourses_receipt_detail_cancel),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
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
                    LbButton(
                        onClick = { onIntent(ReceiptDetailIntent.DownloadClicked) },
                        colors = LbButtonDefaults.whiteColors(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_download),
                            contentDescription = null,
                            tint = LbColors.Ink,
                            modifier = Modifier.size(17.dp),
                        )
                        Text(
                            text = stringResource(R.string.savedcourses_receipt_detail_download),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    LbButton(
                        onClick = { onIntent(ReceiptDetailIntent.DeleteClicked) },
                        colors = receiptDetailDangerColors(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.savedcourses_receipt_detail_delete),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }

    if (state.showDeleteConfirm) {
        ReceiptDeleteConfirmSheet(
            onDismiss = { onIntent(ReceiptDetailIntent.DeleteDismissed) },
            onConfirm = { onIntent(ReceiptDetailIntent.DeleteConfirmed) },
        )
    }
}

@Composable
private fun receiptDetailDangerColors() = ButtonDefaults.buttonColors(
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

@Composable
private fun ReceiptDetailTopBar(
    onBackClick: () -> Unit,
    showEdit: Boolean,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .height(56.dp)
            .background(LbColors.Paper),
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_back),
                contentDescription = stringResource(R.string.savedcourses_receipt_back_content_description),
                tint = Color.Unspecified,
            )
        }
        Text(
            text = stringResource(R.string.savedcourses_receipt_detail_title),
            color = LbColors.Ink,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (showEdit) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.savedcourses_receipt_detail_edit_content_description),
                    tint = LbColors.Ink2,
                    modifier = Modifier.size(19.dp),
                )
            }
        }
    }
    HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceiptDeleteConfirmSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_title),
                color = LbColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_body),
                color = LbColors.Ink2,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(bottom = 20.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                LbButton(
                    onClick = onConfirm,
                    colors = receiptDetailDangerColors(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_ok),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                LbButton(
                    onClick = onDismiss,
                    colors = LbButtonDefaults.whiteColors(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.savedcourses_receipt_detail_delete_confirm_cancel),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptDetailScreenPreview() {
    ReceiptDetailScreenContent(
        state = ReceiptDetailUiState(
            isLoading = false,
            merchantName = "담양앞집",
            amount = "18000",
            paidDate = "2026-07-17",
            imageUrl = null,
        ),
        imageLoader = ImageLoader.Builder(LocalContext.current).build(),
        snackbarHost = {},
        onBack = {},
        onIntent = {},
    )
}
