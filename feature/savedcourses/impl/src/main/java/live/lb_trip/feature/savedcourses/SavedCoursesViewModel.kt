package live.lb_trip.feature.savedcourses

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.GetSavedCoursesUseCase

@HiltViewModel
class SavedCoursesViewModel @Inject constructor(
    private val getSavedCoursesUseCase: GetSavedCoursesUseCase,
) : BaseViewModel<SavedCoursesUiState, SavedCoursesIntent, SavedCoursesSideEffect>(SavedCoursesUiState()) {

    init {
        viewModelScope.launch { loadCourses() }
    }

    override fun onIntent(intent: SavedCoursesIntent) {
        when (intent) {
            SavedCoursesIntent.Retry -> viewModelScope.launch { loadCourses() }
        }
    }

    private suspend fun loadCourses() {
        updateState { it.copy(isLoading = true) }
        getSavedCoursesUseCase()
            .onSuccess { result ->
                val summaries = result.courses.map {
                    SavedCourseSummary(savedCourseId = it.savedCourseId, courseName = it.courseName, status = it.status)
                }
                updateState { it.copy(isLoading = false, courses = summaries.toPersistentList()) }
            }
            .onFailure {
                updateState { it.copy(isLoading = false) }
                postSideEffect(SavedCoursesSideEffect.ShowLoadError)
            }
    }
}
