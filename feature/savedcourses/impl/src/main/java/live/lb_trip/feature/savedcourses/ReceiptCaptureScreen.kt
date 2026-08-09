package live.lb_trip.feature.savedcourses

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R as DesignSystemR
import live.lb_trip.core.designsystem.component.LbButton
import live.lb_trip.core.designsystem.component.LbButtonDefaults
import live.lb_trip.core.designsystem.component.LbInputField
import live.lb_trip.core.designsystem.component.LbTopBar

@Composable
internal fun ReceiptCaptureScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptCaptureViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scanErrorMessage = stringResource(R.string.savedcourses_receipt_error_scan)
    val submitErrorMessage = stringResource(R.string.savedcourses_receipt_error_submit)
    val invalidInputMessage = stringResource(R.string.savedcourses_receipt_error_invalid)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                ReceiptCaptureSideEffect.ShowScanError -> launch { snackbarHostState.showSnackbar(scanErrorMessage) }
                ReceiptCaptureSideEffect.ShowSubmitError -> launch { snackbarHostState.showSnackbar(submitErrorMessage) }
                ReceiptCaptureSideEffect.ShowInvalidInput -> launch { snackbarHostState.showSnackbar(invalidInputMessage) }
                ReceiptCaptureSideEffect.NavigateBackWithSuccess -> onSubmitted()
            }
        }
    }

    ReceiptCaptureScreenContent(
        state = state,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBack = onBack,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun ReceiptCaptureScreenContent(
    state: ReceiptCaptureUiState,
    snackbarHost: @Composable () -> Unit,
    onBack: () -> Unit,
    onIntent: (ReceiptCaptureIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            LbTopBar(
                title = stringResource(R.string.savedcourses_receipt_title),
                onBackClick = onBack,
                backContentDescription = stringResource(R.string.savedcourses_receipt_back_content_description),
            )
        },
        snackbarHost = snackbarHost,
        containerColor = LbColors.Paper,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (state.step) {
                ReceiptCaptureStep.PICK -> ReceiptPickStep(onIntent = onIntent)
                ReceiptCaptureStep.SCANNING -> ReceiptLoadingStep(labelRes = R.string.savedcourses_receipt_scanning)
                ReceiptCaptureStep.VERIFY, ReceiptCaptureStep.SUBMITTING ->
                    ReceiptVerifyStep(state = state, onIntent = onIntent)
            }
        }
    }
}

@Composable
private fun ReceiptPickStep(onIntent: (ReceiptCaptureIntent) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }
            if (bytes != null) {
                val extension = context.contentResolver.getType(uri)
                    ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
                    ?: "jpg"
                onIntent(ReceiptCaptureIntent.ImagePicked(bytes = bytes, fileName = "receipt.$extension"))
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(DesignSystemR.drawable.ic_route),
            contentDescription = null,
            tint = LbColors.Green,
            modifier = Modifier.padding(bottom = 20.dp),
        )
        Text(
            text = stringResource(R.string.savedcourses_receipt_pick_guide),
            color = LbColors.Ink2,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp),
        )
        LbButton(
            onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            colors = LbButtonDefaults.greenColors(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.savedcourses_receipt_pick_button),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ReceiptLoadingStep(@StringRes labelRes: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = LbColors.Green)
        Text(
            text = stringResource(labelRes),
            color = LbColors.Ink2,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 14.dp),
        )
    }
}

@Composable
private fun ReceiptVerifyStep(
    state: ReceiptCaptureUiState,
    onIntent: (ReceiptCaptureIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSubmitting = state.step == ReceiptCaptureStep.SUBMITTING
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = stringResource(R.string.savedcourses_receipt_verify_title),
                color = LbColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            LbInputField(
                value = state.merchantName,
                onValueChange = { onIntent(ReceiptCaptureIntent.MerchantNameChanged(it)) },
                label = stringResource(R.string.savedcourses_receipt_field_merchant),
                placeholder = stringResource(R.string.savedcourses_receipt_field_merchant),
                required = true,
            )
            LbInputField(
                value = state.amount,
                onValueChange = { onIntent(ReceiptCaptureIntent.AmountChanged(it)) },
                label = stringResource(R.string.savedcourses_receipt_field_amount),
                placeholder = "0",
                required = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            LbInputField(
                value = state.paidDate,
                onValueChange = { onIntent(ReceiptCaptureIntent.PaidDateChanged(it)) },
                label = stringResource(R.string.savedcourses_receipt_field_date),
                placeholder = "YYYY-MM-DD",
                required = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        HorizontalDivider(color = LbColors.LineSoft, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 14.dp, vertical = 11.dp),
        ) {
            LbButton(
                onClick = { onIntent(ReceiptCaptureIntent.SubmitClicked) },
                enabled = !isSubmitting,
                colors = LbButtonDefaults.greenColors(),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.savedcourses_receipt_submit),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptCaptureScreenPreview() {
    ReceiptCaptureScreenContent(
        state = ReceiptCaptureUiState(step = ReceiptCaptureStep.PICK),
        snackbarHost = {},
        onBack = {},
        onIntent = {},
    )
}
