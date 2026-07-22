package live.lb_trip.feature.home

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
class HomeViewModel @Inject constructor(
    private val getRecommendedRegionsUseCase: GetRecommendedRegionsUseCase,
    private val getRegionCoursesUseCase: GetRegionCoursesUseCase,
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
        loadAllCourses()
            .onSuccess { courses ->
                updateState { it.copy(isLoadingCourses = false, courses = courses.toPersistentList()) }
            }
            .onFailure {
                updateState { it.copy(isLoadingCourses = false) }
                postSideEffect(HomeSideEffect.ShowCoursesLoadError)
            }
    }

    private suspend fun loadAllCourses(): Result<List<HomeCourseSummary>> = getRecommendedRegionsUseCase().map { regions ->
        coroutineScope {
            regions
                .map { region -> async { region to getRegionCoursesUseCase(region.id).getOrDefault(emptyList()) } }
                .awaitAll()
                .flatMap { (region, courses) ->
                    courses.map { course ->
                        HomeCourseSummary(
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
