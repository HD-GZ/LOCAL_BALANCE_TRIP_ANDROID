package live.lb_trip.feature.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.GetSavedCoursesUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSavedCoursesUseCase: GetSavedCoursesUseCase,
) : BaseViewModel<HomeUiState, HomeIntent, HomeSideEffect>(HomeUiState()) {

    init {
        viewModelScope.launch { loadCourses() }
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Retry -> viewModelScope.launch { loadCourses() }
        }
    }

    private suspend fun loadCourses() {
        updateState { it.copy(isLoadingCourses = true) }
        getSavedCoursesUseCase()
            .onSuccess { result ->
                val summaries = result.courses.map {
                    HomeCourseSummary(savedCourseId = it.savedCourseId, courseName = it.courseName, status = it.status)
                }
                updateState { it.copy(isLoadingCourses = false, courses = summaries.toPersistentList()) }
            }
            .onFailure {
                updateState { it.copy(isLoadingCourses = false) }
                postSideEffect(HomeSideEffect.ShowCoursesLoadError)
            }
    }
}
