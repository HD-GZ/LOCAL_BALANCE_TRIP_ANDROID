package live.lb_trip.feature.savedcourses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.DeleteReceiptUseCase
import live.lb_trip.domain.usecase.GetReceiptDetailUseCase
import live.lb_trip.domain.usecase.GetReceiptDownloadUrlUseCase
import live.lb_trip.domain.usecase.UpdateReceiptUseCase

@HiltViewModel
class ReceiptDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReceiptDetailUseCase: GetReceiptDetailUseCase,
    private val updateReceiptUseCase: UpdateReceiptUseCase,
    private val deleteReceiptUseCase: DeleteReceiptUseCase,
    private val getReceiptDownloadUrlUseCase: GetReceiptDownloadUrlUseCase,
) : BaseViewModel<ReceiptDetailUiState, ReceiptDetailIntent, ReceiptDetailSideEffect>(ReceiptDetailUiState()) {

    private val route = savedStateHandle.toRoute<ReceiptDetailRoute>()
    private val savedCourseId = route.savedCourseId
    private val receiptId = route.receiptId

    init {
        viewModelScope.launch { load() }
    }

    override fun onIntent(intent: ReceiptDetailIntent) {
        when (intent) {
            ReceiptDetailIntent.Retry -> viewModelScope.launch { load() }
            ReceiptDetailIntent.EditClicked -> updateState {
                it.copy(
                    isEditing = true,
                    editMerchantName = it.merchantName,
                    editAmount = it.amount,
                    editPaidDate = it.paidDate,
                )
            }
            ReceiptDetailIntent.EditCancelled -> updateState { it.copy(isEditing = false) }
            is ReceiptDetailIntent.MerchantNameChanged -> updateState { it.copy(editMerchantName = intent.value) }
            is ReceiptDetailIntent.AmountChanged ->
                updateState { it.copy(editAmount = intent.value.filter(Char::isDigit)) }
            is ReceiptDetailIntent.PaidDateChanged -> updateState { it.copy(editPaidDate = intent.value) }
            ReceiptDetailIntent.SaveClicked -> viewModelScope.launch { save() }
            ReceiptDetailIntent.DownloadClicked -> viewModelScope.launch { download() }
            ReceiptDetailIntent.DeleteClicked -> updateState { it.copy(showDeleteConfirm = true) }
            ReceiptDetailIntent.DeleteDismissed -> updateState { it.copy(showDeleteConfirm = false) }
            ReceiptDetailIntent.DeleteConfirmed -> viewModelScope.launch { delete() }
        }
    }

    private suspend fun load() {
        updateState { it.copy(isLoading = true) }
        getReceiptDetailUseCase(savedCourseId, receiptId)
            .onSuccess { detail ->
                updateState {
                    it.copy(
                        isLoading = false,
                        merchantName = detail.merchantName,
                        amount = detail.amount.toString(),
                        paidDate = detail.paidDate,
                        imageUrl = detail.imageUrl,
                    )
                }
            }
            .onFailure {
                updateState { it.copy(isLoading = false) }
                postSideEffect(ReceiptDetailSideEffect.ShowLoadError)
            }
    }

    private suspend fun save() {
        val state = currentState
        val amount = state.editAmount.toIntOrNull()
        if (amount == null || state.editMerchantName.isBlank() || state.editPaidDate.isBlank()) {
            postSideEffect(ReceiptDetailSideEffect.ShowInvalidInput)
            return
        }
        updateState { it.copy(isSaving = true) }
        updateReceiptUseCase(savedCourseId, receiptId, state.editMerchantName, amount, state.editPaidDate)
            .onSuccess { detail ->
                updateState {
                    it.copy(
                        isSaving = false,
                        isEditing = false,
                        merchantName = detail.merchantName,
                        amount = detail.amount.toString(),
                        paidDate = detail.paidDate,
                        imageUrl = detail.imageUrl,
                    )
                }
                postSideEffect(ReceiptDetailSideEffect.ShowUpdated)
            }
            .onFailure {
                updateState { it.copy(isSaving = false) }
                postSideEffect(ReceiptDetailSideEffect.ShowUpdateError)
            }
    }

    private suspend fun delete() {
        updateState { it.copy(showDeleteConfirm = false) }
        deleteReceiptUseCase(savedCourseId, receiptId)
            .onSuccess { postSideEffect(ReceiptDetailSideEffect.NavigateBackWithDeleted) }
            .onFailure { postSideEffect(ReceiptDetailSideEffect.ShowDeleteError) }
    }

    private suspend fun download() {
        getReceiptDownloadUrlUseCase(savedCourseId, receiptId)
            .onSuccess { url -> postSideEffect(ReceiptDetailSideEffect.DownloadUrlReady(url)) }
            .onFailure { postSideEffect(ReceiptDetailSideEffect.ShowDownloadError) }
    }
}
