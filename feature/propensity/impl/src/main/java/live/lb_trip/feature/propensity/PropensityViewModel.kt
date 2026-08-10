package live.lb_trip.feature.propensity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.ValueConsumption
import live.lb_trip.domain.usecase.GetPropensityResultUseCase
import live.lb_trip.domain.usecase.SubmitPropensityUseCase

@HiltViewModel
class PropensityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val submitPropensityUseCase: SubmitPropensityUseCase,
    private val getPropensityResultUseCase: GetPropensityResultUseCase,
) : BaseViewModel<PropensityUiState, PropensityIntent, PropensitySideEffect>(PropensityUiState()) {

    init {
        val forceNew = savedStateHandle.toRoute<PropensityRoute>().forceNew
        if (!forceNew) {
            viewModelScope.launch { loadExistingResult() }
        }
    }

    private suspend fun loadExistingResult() {
        getPropensityResultUseCase().onSuccess { result ->
            updateState {
                it.copy(
                    locality = result.preference.locality,
                    frugality = result.preference.frugality,
                    experientiality = result.preference.experientiality,
                    vitality = result.preference.vitality,
                    sociality = result.preference.sociality,
                    accommodation = result.valueConsumption.accommodation,
                    food = result.valueConsumption.food,
                    experience = result.valueConsumption.experience,
                    transportation = result.valueConsumption.transportation,
                    cafeExhibition = result.valueConsumption.cafeExhibition,
                    resultType = result.type,
                    resultCode = result.code,
                    resultDescription = result.description,
                    resultImageUrl = result.imageUrl,
                )
            }
            postSideEffect(PropensitySideEffect.NavigateToResult)
        }
    }

    override fun onIntent(intent: PropensityIntent) {
        when (intent) {
            is PropensityIntent.LocalityChanged -> updateState { it.copy(locality = intent.value) }
            is PropensityIntent.FrugalityChanged -> updateState { it.copy(frugality = intent.value) }
            is PropensityIntent.ExperientialityChanged -> updateState { it.copy(experientiality = intent.value) }
            is PropensityIntent.VitalityChanged -> updateState { it.copy(vitality = intent.value) }
            is PropensityIntent.SocialityChanged -> updateState { it.copy(sociality = intent.value) }
            is PropensityIntent.AccommodationChanged -> updateState { it.copy(accommodation = intent.value) }
            is PropensityIntent.FoodChanged -> updateState { it.copy(food = intent.value) }
            is PropensityIntent.ExperienceChanged -> updateState { it.copy(experience = intent.value) }
            is PropensityIntent.TransportationChanged -> updateState { it.copy(transportation = intent.value) }
            is PropensityIntent.CafeExhibitionChanged -> updateState { it.copy(cafeExhibition = intent.value) }
            PropensityIntent.SubmitAndViewResult -> submitAndViewResult()
            PropensityIntent.RestartDiagnosis -> restartDiagnosis()
            PropensityIntent.CourseRecommendationClicked -> postSideEffect(PropensitySideEffect.NavigateToRecommendation)
        }
    }

    private fun submitAndViewResult() {
        viewModelScope.launch {
            val current = currentState
            updateState { it.copy(isLoading = true, errorMessage = null) }
            submitPropensityUseCase(
                preference = Preference(
                    locality = current.locality,
                    frugality = current.frugality,
                    experientiality = current.experientiality,
                    vitality = current.vitality,
                    sociality = current.sociality,
                ),
                valueConsumption = ValueConsumption(
                    accommodation = current.accommodation,
                    food = current.food,
                    experience = current.experience,
                    transportation = current.transportation,
                    cafeExhibition = current.cafeExhibition,
                ),
            ).onSuccess { result ->
                updateState {
                    it.copy(
                        resultType = result.type,
                        resultCode = result.code,
                        resultDescription = result.description,
                        resultImageUrl = result.imageUrl,
                    )
                }
                postSideEffect(PropensitySideEffect.NavigateToResult)
            }.onFailure { throwable ->
                if (throwable is LbTripPropensityException.UnauthenticatedException) {
                    updateState { it.copy(errorMessage = "로그인 후 진단 결과를 받아볼 수 있어요. 로그인하고 다시 제출해 주세요.") }
                    postSideEffect(PropensitySideEffect.NavigateToSignin)
                } else {
                    val message = when (throwable) {
                        is LbTripPropensityException.InvalidInputException -> "입력값을 다시 확인해 주세요."
                        else -> "진단 결과를 가져오지 못했어요. 잠시 후 다시 시도해 주세요."
                    }
                    updateState { it.copy(errorMessage = message) }
                }
            }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun restartDiagnosis() {
        updateState {
            it.copy(
                locality = 3, frugality = 3, experientiality = 3, vitality = 3, sociality = 3,
                accommodation = 3, food = 3, experience = 3, transportation = 3, cafeExhibition = 3,
                resultType = null, resultCode = null, resultDescription = null, resultImageUrl = null,
            )
        }
        postSideEffect(PropensitySideEffect.RestartToPreference)
    }
}
