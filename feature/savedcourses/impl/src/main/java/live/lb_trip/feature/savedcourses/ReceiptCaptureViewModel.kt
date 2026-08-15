package live.lb_trip.feature.savedcourses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.RegisterReceiptUseCase
import live.lb_trip.domain.usecase.ScanReceiptUseCase

@HiltViewModel
class ReceiptCaptureViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scanReceiptUseCase: ScanReceiptUseCase,
    private val registerReceiptUseCase: RegisterReceiptUseCase,
) : BaseViewModel<ReceiptCaptureUiState, ReceiptCaptureIntent, ReceiptCaptureSideEffect>(ReceiptCaptureUiState()) {

    private val savedCourseId: Long = savedStateHandle.toRoute<ReceiptCaptureRoute>().savedCourseId

    override fun onIntent(intent: ReceiptCaptureIntent) {
        when (intent) {
            is ReceiptCaptureIntent.ImagePicked -> viewModelScope.launch { scan(intent.bytes, intent.fileName) }
            is ReceiptCaptureIntent.MerchantNameChanged -> updateState { it.copy(merchantName = intent.value) }
            is ReceiptCaptureIntent.AmountChanged ->
                updateState { it.copy(amount = intent.value.filter(Char::isDigit)) }
            is ReceiptCaptureIntent.PaidDateChanged -> updateState { it.copy(paidDate = intent.value) }
            ReceiptCaptureIntent.SubmitClicked -> viewModelScope.launch { submit() }
        }
    }

    private suspend fun scan(bytes: ByteArray, fileName: String) {
        updateState { it.copy(step = ReceiptCaptureStep.SCANNING) }
        scanReceiptUseCase(savedCourseId, bytes, fileName)
            .onSuccess { scan ->
                updateState {
                    it.copy(
                        step = ReceiptCaptureStep.VERIFY,
                        imageId = scan.imageId,
                        merchantName = scan.merchantName.orEmpty(),
                        amount = scan.amount?.toString().orEmpty(),
                        paidDate = scan.paidDate.orEmpty(),
                    )
                }
            }
            .onFailure {
                postSideEffect(ReceiptCaptureSideEffect.ShowScanError)
            }
    }

    private suspend fun submit() {
        val state = currentState
        val hasBlankField = state.merchantName.isBlank() || state.paidDate.isBlank()
        val imageId = state.imageId
        val amount = state.amount.toIntOrNull()
        if (imageId == null || amount == null || hasBlankField) {
            postSideEffect(ReceiptCaptureSideEffect.ShowInvalidInput)
            return
        }

        updateState { it.copy(step = ReceiptCaptureStep.SUBMITTING) }
        registerReceiptUseCase(savedCourseId, imageId, state.merchantName, amount, state.paidDate)
            .onSuccess { postSideEffect(ReceiptCaptureSideEffect.NavigateBackWithSuccess) }
            .onFailure {
                updateState { it.copy(step = ReceiptCaptureStep.VERIFY) }
                postSideEffect(ReceiptCaptureSideEffect.ShowSubmitError)
            }
    }
}
