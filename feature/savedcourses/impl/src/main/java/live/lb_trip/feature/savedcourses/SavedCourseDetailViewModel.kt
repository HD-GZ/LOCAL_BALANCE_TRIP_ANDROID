package live.lb_trip.feature.savedcourses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.CourseBenefit
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.usecase.GetReceiptsUseCase
import live.lb_trip.domain.usecase.GetSavedCourseDetailUseCase
import live.lb_trip.domain.usecase.GetSavedCourseReportUseCase

@HiltViewModel
class SavedCourseDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getSavedCourseDetailUseCase: GetSavedCourseDetailUseCase,
    private val getReceiptsUseCase: GetReceiptsUseCase,
    private val getSavedCourseReportUseCase: GetSavedCourseReportUseCase,
) : BaseViewModel<SavedCourseDetailUiState, SavedCourseDetailIntent, SavedCourseDetailSideEffect>(
    SavedCourseDetailUiState(),
) {

    private val savedCourseId: Long = savedStateHandle.toRoute<SavedCourseDetailRoute>().savedCourseId

    init {
        viewModelScope.launch { load() }
    }

    override fun onIntent(intent: SavedCourseDetailIntent) {
        when (intent) {
            is SavedCourseDetailIntent.TabSelected -> updateState { it.copy(selectedTab = intent.tab) }
            is SavedCourseDetailIntent.StopToggled -> toggleStopExpanded(intent.index)
            is SavedCourseDetailIntent.BenefitClicked ->
                postSideEffect(SavedCourseDetailSideEffect.OpenBenefitUrl(intent.url))
            SavedCourseDetailIntent.TourStartClicked ->
                postSideEffect(SavedCourseDetailSideEffect.NavigateToTour(savedCourseId))
            SavedCourseDetailIntent.RegisterReceiptClicked ->
                postSideEffect(SavedCourseDetailSideEffect.NavigateToReceiptCapture(savedCourseId))
            SavedCourseDetailIntent.Retry -> viewModelScope.launch { load() }
            SavedCourseDetailIntent.ReceiptRegistered -> viewModelScope.launch { reloadReceipts() }
        }
    }

    private suspend fun load() {
        updateState { it.copy(isLoading = true, isReceiptsLoading = true, isReportLoading = true) }
        coroutineScope {
            val detailDeferred = async { getSavedCourseDetailUseCase(savedCourseId) }
            val receiptsDeferred = async { getReceiptsUseCase(savedCourseId) }
            val reportDeferred = async { getSavedCourseReportUseCase(savedCourseId) }
            val detailResult = detailDeferred.await()
            val receiptsResult = receiptsDeferred.await()
            val reportResult = reportDeferred.await()

            detailResult
                .onSuccess { detail ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            regionName = detail.regionName,
                            title = detail.title,
                            status = detail.status,
                            stops = detail.places.map(CoursePlace::toSavedCourseStop).toPersistentList(),
                            benefits = detail.benefits.map(CourseBenefit::toSavedCourseBenefit).toPersistentList(),
                        )
                    }
                    if (detail.places.isEmpty()) {
                        postSideEffect(
                            SavedCourseDetailSideEffect.ShowLoadError(SavedCourseDetailLoadErrorReason.EmptyPlaces),
                        )
                    }
                }
                .onFailure { throwable ->
                    updateState { it.copy(isLoading = false) }
                    postSideEffect(SavedCourseDetailSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
                }

            receiptsResult
                .onSuccess { summary ->
                    updateState {
                        it.copy(
                            isReceiptsLoading = false,
                            hasLoadedReceipts = true,
                            receiptTotalAmount = summary.totalAmount,
                            receipts = summary.receipts.map(Receipt::toSavedCourseReceipt).toPersistentList(),
                        )
                    }
                }
                .onFailure {
                    updateState { it.copy(isReceiptsLoading = false) }
                    postSideEffect(SavedCourseDetailSideEffect.ShowReceiptsLoadError)
                }

            applyReportResult(reportResult)
        }
    }

    private fun applyReportResult(result: Result<SavedCourseReport>) {
        result
            .onSuccess { report ->
                updateState {
                    it.copy(
                        isReportLoading = false,
                        isReportAvailable = true,
                        reportVisitedPlaceCount = report.visitedPlaceCount,
                        reportTotalSpentAmount = report.totalSpentAmount,
                        reportTourEndedAt = report.tourEndedAt,
                    )
                }
            }
            .onFailure { throwable ->
                updateState { it.copy(isReportLoading = false, isReportAvailable = false) }
                // Tour not yet ended is expected for BEFORE_TRIP/TRAVELING courses, not an error to surface.
                if (throwable !is LbTripSavedCourseException.TourReportNotAvailableException) {
                    postSideEffect(SavedCourseDetailSideEffect.ShowReportLoadError)
                }
            }
    }

    private suspend fun reloadReceipts() {
        updateState { it.copy(isReceiptsLoading = true) }
        getReceiptsUseCase(savedCourseId)
            .onSuccess { summary ->
                updateState {
                    it.copy(
                        isReceiptsLoading = false,
                        hasLoadedReceipts = true,
                        receiptTotalAmount = summary.totalAmount,
                        receipts = summary.receipts.map(Receipt::toSavedCourseReceipt).toPersistentList(),
                    )
                }
            }
            .onFailure {
                updateState { it.copy(isReceiptsLoading = false) }
                postSideEffect(SavedCourseDetailSideEffect.ShowReceiptsLoadError)
            }
    }

    private fun toggleStopExpanded(index: Int) {
        updateState {
            it.copy(
                expandedStopIndices = if (index in it.expandedStopIndices) {
                    it.expandedStopIndices - index
                } else {
                    it.expandedStopIndices + index
                },
            )
        }
    }
}

private fun CoursePlace.toSavedCourseStop(): SavedCourseStop = SavedCourseStop(
    order = order,
    name = name,
    hasAudioGuide = hasAudio,
    walkDuration = walkMinutes?.let { "${it}분" },
    description = description,
)

private fun CourseBenefit.toSavedCourseBenefit(): SavedCourseBenefit =
    SavedCourseBenefit(title = title, description = description, url = url)

private fun Receipt.toSavedCourseReceipt(): SavedCourseReceipt =
    SavedCourseReceipt(receiptId = receiptId, merchantName = merchantName, amount = amount, paidDate = paidDate)

private fun loadErrorReasonFor(throwable: Throwable?): SavedCourseDetailLoadErrorReason = when (throwable) {
    is LbTripSavedCourseException.SavedCourseNotFoundException -> SavedCourseDetailLoadErrorReason.CourseNotFound
    else -> SavedCourseDetailLoadErrorReason.Unknown
}
