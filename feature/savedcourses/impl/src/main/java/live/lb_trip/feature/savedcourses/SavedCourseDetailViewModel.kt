package live.lb_trip.feature.savedcourses

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.savedcourse.LbTripSavedCourseException
import live.lb_trip.domain.model.CourseBenefit
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.usecase.GetReceiptsUseCase
import live.lb_trip.domain.usecase.GetSavedCourseDetailUseCase
import live.lb_trip.domain.usecase.GetSavedCourseReportUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase
import live.lb_trip.domain.usecase.IssueShareTokenUseCase

@HiltViewModel(assistedFactory = SavedCourseDetailViewModel.Factory::class)
class SavedCourseDetailViewModel @AssistedInject constructor(
    @Assisted private val savedCourseId: Long,
    private val getSavedCourseDetailUseCase: GetSavedCourseDetailUseCase,
    private val getReceiptsUseCase: GetReceiptsUseCase,
    private val getSavedCourseReportUseCase: GetSavedCourseReportUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val issueShareTokenUseCase: IssueShareTokenUseCase,
    ) : BaseViewModel<SavedCourseDetailUiState, SavedCourseDetailIntent, SavedCourseDetailSideEffect>(
    SavedCourseDetailUiState(),
) {

    @AssistedFactory
    interface Factory {
        fun create(savedCourseId: Long): SavedCourseDetailViewModel
    }

    init {
        updateState { it.copy(savedCourseId = savedCourseId) }
        viewModelScope.launch { load() }
        viewModelScope.launch {
            getUserProfileUseCase().onSuccess { profile ->
                updateState { it.copy(username = profile.name) }
            }
        }
    }

    override fun onIntent(intent: SavedCourseDetailIntent) {
        when (intent) {
            is SavedCourseDetailIntent.TabSelected -> updateState { it.copy(selectedTab = intent.tab) }
            is SavedCourseDetailIntent.StopToggled -> toggleStopExpanded(intent.index)
            is SavedCourseDetailIntent.BenefitClicked ->
                postSideEffect(SavedCourseDetailSideEffect.OpenBenefitUrl(intent.url))
            SavedCourseDetailIntent.TourStartClicked ->
                postSideEffect(SavedCourseDetailSideEffect.NavigateToTour(savedCourseId))
            SavedCourseDetailIntent.Retry -> viewModelScope.launch { load() }
            SavedCourseDetailIntent.ReceiptRegistered -> viewModelScope.launch {
                coroutineScope {
                    launch { reloadReceipts() }
                    launch { reloadReport() }
                }
            }
            is SavedCourseDetailIntent.KakaoShareClicked -> viewModelScope.launch {
                issueShareTokenUseCase(savedCourseId)
                    .onSuccess { shareToken ->
                        postSideEffect(
                            SavedCourseDetailSideEffect.LaunchKakaoShare(
                                title = intent.title,
                                description = intent.description,
                                imageUrl = intent.imageUrl,
                                shareToken = shareToken.token,
                            ),
                        )
                    }
                    .onFailure {
                        postSideEffect(SavedCourseDetailSideEffect.ShowKakaoShareError)
                    }
            }
        }
    }

    private suspend fun load() {
        updateState { it.copy(isLoading = true, isReceiptsLoading = true, isReportLoading = true) }
        coroutineScope {
            launch { applyDetailResult(getSavedCourseDetailUseCase(savedCourseId)) }
            launch { applyReceiptsResult(getReceiptsUseCase(savedCourseId)) }
            launch { applyReportResult(fetchReportWithRetry()) }
        }
    }

    private fun applyDetailResult(result: Result<SavedCourseDetail>) {
        result
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
    }

    fun setLoading(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private fun applyReceiptsResult(result: Result<ReceiptSummary>) {
        result
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

    private suspend fun fetchReportWithRetry(): Result<SavedCourseReport> {
        var result = getSavedCourseReportUseCase(savedCourseId)
        repeat(REPORT_RETRY_ATTEMPTS) {
            if (result.exceptionOrNull() !is LbTripSavedCourseException.TourReportNotAvailableException) return result
            delay(REPORT_RETRY_DELAY_MILLIS.milliseconds)
            result = getSavedCourseReportUseCase(savedCourseId)
        }
        return result
    }

    private fun applyReportResult(result: Result<SavedCourseReport>) {
        result
            .onSuccess { report ->
                updateState {
                    it.copy(
                        isReportLoading = false,
                        isReportAvailable = true,
                        reportImageUrl = report.imageUrl,
                        reportVisitedPlaceCount = report.visitedPlaceCount,
                        reportTotalSpentAmount = report.totalSpentAmount,
                        reportTourEndedAt = report.tourEndedAt,
                        reportDistanceWalkedMeters = report.distanceWalkedMeters,
                        reportStepCount = report.stepCount,
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
        applyReceiptsResult(getReceiptsUseCase(savedCourseId))
    }

    private suspend fun reloadReport() {
        updateState { it.copy(isReportLoading = true) }
        applyReportResult(fetchReportWithRetry())
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

private const val REPORT_RETRY_ATTEMPTS = 2
private const val REPORT_RETRY_DELAY_MILLIS = 700L
