package live.lb_trip.feature.savedcourses

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.usecase.GetRecommendedRegionsUseCase
import live.lb_trip.domain.usecase.GetRegionCoursesUseCase

@HiltViewModel
class SavedCoursesViewModel @Inject constructor(
    private val getRecommendedRegionsUseCase: GetRecommendedRegionsUseCase,
    private val getRegionCoursesUseCase: GetRegionCoursesUseCase,
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
        loadAllCourses()
            .onSuccess { courses ->
                updateState { it.copy(isLoading = false, courses = courses.toPersistentList()) }
            }
            .onFailure {
                updateState { it.copy(isLoading = false) }
                postSideEffect(SavedCoursesSideEffect.ShowLoadError)
            }
    }

    private suspend fun loadAllCourses(): Result<List<SavedCourseSummary>> = getRecommendedRegionsUseCase().map { regions ->
        coroutineScope {
            regions
                .map { region -> async { region to getRegionCoursesUseCase(region.id).getOrDefault(emptyList()) } }
                .awaitAll()
                .flatMap { (region, courses) ->
                    courses.map { course ->
                        SavedCourseSummary(
                            courseId = course.id,
                            regionName = region.name,
                            title = course.title,
                            reason = course.reason,
                        )
                    }
                }
        }
    }
}
