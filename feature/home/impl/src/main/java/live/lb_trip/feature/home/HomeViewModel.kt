package live.lb_trip.feature.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.HomeFeedItem
import live.lb_trip.domain.usecase.GetHomeFeedUseCase
import live.lb_trip.domain.usecase.GetHomeHeroUseCase
import live.lb_trip.domain.usecase.GetHomeIncentivesUseCase
import live.lb_trip.domain.usecase.GetHomePopularCoursesUseCase
import live.lb_trip.domain.usecase.GetHomeProfileSummaryUseCase
import live.lb_trip.domain.usecase.GetHomeProfileTypesUseCase
import live.lb_trip.domain.usecase.GetTokensUseCase

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTokensUseCase: GetTokensUseCase,
    private val getHomeHeroUseCase: GetHomeHeroUseCase,
    private val getHomeProfileTypesUseCase: GetHomeProfileTypesUseCase,
    private val getHomeProfileSummaryUseCase: GetHomeProfileSummaryUseCase,
    private val getHomeIncentivesUseCase: GetHomeIncentivesUseCase,
    private val getHomePopularCoursesUseCase: GetHomePopularCoursesUseCase,
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
) : BaseViewModel<HomeUiState, HomeIntent, HomeSideEffect>(HomeUiState()) {

    init {
        viewModelScope.launch { loadHero() }
        viewModelScope.launch { loadIncentives() }
        viewModelScope.launch {
            getTokensUseCase()
                .map { it != null }
                .distinctUntilChanged()
                .collect { loggedIn ->
                    updateState { it.copy(isLoggedIn = loggedIn) }
                    loadTypeSection(loggedIn)
                    loadFeedSection(loggedIn)
                }
        }
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Retry -> viewModelScope.launch { loadFeedSection(currentState.isLoggedIn) }
            is HomeIntent.IncentiveClicked -> postSideEffect(HomeSideEffect.OpenUrl(intent.card.url))
            is HomeIntent.FeedItemClicked -> handleFeedItemClicked(intent.item)
        }
    }

    private fun handleFeedItemClicked(item: HomeFeedItem) {
        if (item is HomeFeedItem.RecommendedRegionItem) {
            postSideEffect(HomeSideEffect.ShowRecommendedRegionUnavailable)
        }
    }

    private suspend fun loadHero() {
        updateState { it.copy(isHeroLoading = true) }
        getHomeHeroUseCase()
            .onSuccess { items -> updateState { it.copy(isHeroLoading = false, heroItems = items.toPersistentList()) } }
            .onFailure { updateState { it.copy(isHeroLoading = false) } }
    }

    private suspend fun loadIncentives() {
        updateState { it.copy(isIncentivesLoading = true) }
        getHomeIncentivesUseCase()
            .onSuccess { regions ->
                updateState { it.copy(isIncentivesLoading = false, incentiveCards = regions.toIncentiveCards()) }
            }
            .onFailure { updateState { it.copy(isIncentivesLoading = false) } }
    }

    private suspend fun loadTypeSection(loggedIn: Boolean) {
        updateState { it.copy(isTypeSectionLoading = true) }
        if (!loggedIn) {
            loadProfileTypes()
            return
        }
        getHomeProfileSummaryUseCase()
            .onSuccess { summary ->
                updateState { it.copy(isTypeSectionLoading = false, isDiagnosed = true, profileSummary = summary) }
            }
            .onFailure { throwable ->
                if (throwable is LbTripPropensityException.PropensityNotFoundException) {
                    loadProfileTypes()
                } else {
                    updateState { it.copy(isTypeSectionLoading = false) }
                }
            }
    }

    private suspend fun loadProfileTypes() {
        getHomeProfileTypesUseCase()
            .onSuccess { types ->
                updateState {
                    it.copy(
                        isTypeSectionLoading = false,
                        isDiagnosed = false,
                        profileTypes = types.toPersistentList(),
                        profileSummary = null,
                    )
                }
            }
            .onFailure { updateState { it.copy(isTypeSectionLoading = false) } }
    }

    private suspend fun loadFeedSection(loggedIn: Boolean) {
        updateState { it.copy(isFeedLoading = true) }
        if (!loggedIn) {
            getHomePopularCoursesUseCase()
                .onSuccess { courses ->
                    updateState {
                        it.copy(isFeedLoading = false, feed = persistentListOf(), popularCourses = courses.toPersistentList())
                    }
                }
                .onFailure {
                    updateState {
                        it.copy(isFeedLoading = false, feed = persistentListOf(), popularCourses = persistentListOf())
                    }
                }
            return
        }
        getHomeFeedUseCase()
            .onSuccess { items ->
                updateState { it.copy(isFeedLoading = false, feed = items.toPersistentList(), popularCourses = persistentListOf()) }
            }
            .onFailure {
                updateState { it.copy(isFeedLoading = false) }
                postSideEffect(HomeSideEffect.ShowFeedLoadError)
            }
    }
}
