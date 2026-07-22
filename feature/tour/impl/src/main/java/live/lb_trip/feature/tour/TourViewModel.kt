package live.lb_trip.feature.tour

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.recommendation.LbTripRecommendationException
import live.lb_trip.domain.model.CoursePlace
import live.lb_trip.domain.usecase.GetCourseDetailUseCase

@HiltViewModel
class TourViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCourseDetailUseCase: GetCourseDetailUseCase,
) : BaseViewModel<TourUiState, TourIntent, TourSideEffect>(TourUiState()) {

    private val courseId: Long = savedStateHandle.toRoute<TourRoute>().courseId

    init {
        viewModelScope.launch { loadCourseDetail() }
    }

    override fun onIntent(intent: TourIntent) {
        when (intent) {
            TourIntent.NextStopArrived -> advanceToNextStop()
            is TourIntent.StopSelected -> selectStop(intent.index)
            TourIntent.EndTourClicked -> postSideEffect(TourSideEffect.NavigateBack)
            TourIntent.Retry -> viewModelScope.launch { loadCourseDetail() }
        }
    }

    private suspend fun loadCourseDetail() {
        updateState { it.copy(isLoading = true) }
        getCourseDetailUseCase(courseId)
            .onSuccess { detail ->
                updateState {
                    it.copy(
                        isLoading = false,
                        regionName = detail.regionName,
                        title = detail.title,
                        stops = detail.places.map(CoursePlace::toTourStop).toPersistentList(),
                        currentStopIndex = 0,
                    )
                }
                if (detail.places.isEmpty()) {
                    postSideEffect(TourSideEffect.ShowLoadError(TourLoadErrorReason.EmptyPlaces))
                }
            }
            .onFailure { throwable ->
                updateState { it.copy(isLoading = false) }
                postSideEffect(TourSideEffect.ShowLoadError(loadErrorReasonFor(throwable)))
            }
    }

    private fun advanceToNextStop() {
        val lastIndex = currentState.stops.lastIndex
        if (currentState.currentStopIndex >= lastIndex) {
            postSideEffect(TourSideEffect.NavigateBack)
            return
        }
        updateState { it.copy(currentStopIndex = it.currentStopIndex + 1) }
        postSideEffect(TourSideEffect.CollapseSheet)
    }

    private fun selectStop(index: Int) {
        val lastIndex = currentState.stops.lastIndex
        if (index !in 0..lastIndex) return
        updateState { it.copy(currentStopIndex = index) }
        postSideEffect(TourSideEffect.CollapseSheet)
    }
}

private fun CoursePlace.toTourStop(): TourStop = TourStop(
    order = order,
    name = name,
    latitude = latitude,
    longitude = longitude,
    walkMinutesToNext = walkMinutes,
)

private fun loadErrorReasonFor(throwable: Throwable?): TourLoadErrorReason = when (throwable) {
    is LbTripRecommendationException.CourseNotFoundException -> TourLoadErrorReason.CourseNotFound
    else -> TourLoadErrorReason.Unknown
}
