package live.lb_trip.feature.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.model.CourseDetail
import live.lb_trip.domain.usecase.GetSavedCoursesUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSavedCoursesUseCase: GetSavedCoursesUseCase,
) : BaseViewModel<HomeUiState, HomeIntent, Nothing>(HomeUiState()) {

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RefreshSavedCourses -> refreshSavedCourses()
        }
    }

    private fun refreshSavedCourses() {
        viewModelScope.launch {
            updateState { it.copy(isSavedCoursesLoading = true, isSavedCoursesLoadError = false) }
            getSavedCoursesUseCase()
                .onSuccess { courses ->
                    updateState {
                        it.copy(
                            isSavedCoursesLoading = false,
                            savedCourses = courses.map(CourseDetail::toSavedCourseUi).toPersistentList(),
                        )
                    }
                }
                .onFailure {
                    updateState { it.copy(isSavedCoursesLoading = false, isSavedCoursesLoadError = true) }
                }
        }
    }
}

private fun CourseDetail.toSavedCourseUi(): SavedCourseUi = SavedCourseUi(
    courseId = id,
    regionName = regionName,
    title = title,
    stopCount = places.size,
)
