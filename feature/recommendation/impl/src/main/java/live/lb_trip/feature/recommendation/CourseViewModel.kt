package live.lb_trip.feature.recommendation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.recommendation.LbTripRecommendationException
import live.lb_trip.domain.usecase.GetRegionCoursesUseCase

@HiltViewModel
class CourseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRegionCoursesUseCase: GetRegionCoursesUseCase,
) : BaseViewModel<CourseUiState, CourseSideEffect>(CourseUiState()) {

    private val route: CourseRoute = savedStateHandle.toRoute()
    val regionId: Long = route.regionId
    val regionName: String = route.regionName

    init {
        viewModelScope.launch { loadCourses() }
    }

    fun retry() {
        viewModelScope.launch { loadCourses() }
    }

    private suspend fun loadCourses() {
        updateState { it.copy(isLoading = true) }
        getRegionCoursesUseCase(regionId)
            .onSuccess { courses ->
                updateState { it.copy(isLoading = false, courses = courses.toPersistentList()) }
                if (courses.isEmpty()) postSideEffect(CourseSideEffect.ShowError(CourseLoadErrorReason.Empty))
            }
            .onFailure { throwable ->
                updateState { it.copy(isLoading = false) }
                postSideEffect(CourseSideEffect.ShowError(loadErrorReasonFor(throwable)))
            }
    }
}

private fun loadErrorReasonFor(throwable: Throwable?): CourseLoadErrorReason = when (throwable) {
    is LbTripRecommendationException.RegionNotFoundException -> CourseLoadErrorReason.RegionNotFound
    else -> CourseLoadErrorReason.Unknown
}
