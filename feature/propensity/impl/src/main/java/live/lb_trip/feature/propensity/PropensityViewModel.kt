package live.lb_trip.feature.propensity

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.ValueConsumption
import live.lb_trip.domain.usecase.SubmitPropensityUseCase

@HiltViewModel
class PropensityViewModel @Inject constructor(
    private val submitPropensityUseCase: SubmitPropensityUseCase,
) : BaseViewModel<PropensityUiState, PropensitySideEffect>(PropensityUiState()) {

    fun updateLocality(value: Int) = updateState { it.copy(locality = value) }
    fun updateFrugality(value: Int) = updateState { it.copy(frugality = value) }
    fun updateExperientiality(value: Int) = updateState { it.copy(experientiality = value) }
    fun updateVitality(value: Int) = updateState { it.copy(vitality = value) }
    fun updateSociality(value: Int) = updateState { it.copy(sociality = value) }
    fun updateAccommodation(value: Int) = updateState { it.copy(accommodation = value) }
    fun updateFood(value: Int) = updateState { it.copy(food = value) }
    fun updateExperience(value: Int) = updateState { it.copy(experience = value) }
    fun updateTransportation(value: Int) = updateState { it.copy(transportation = value) }
    fun updateCafeExhibition(value: Int) = updateState { it.copy(cafeExhibition = value) }

    fun navigateBack() {
        when (currentState.step) {
            PropensityStep.Preference -> postSideEffect(PropensitySideEffect.NavigateBack)
            PropensityStep.ValueConsumption -> updateState { it.copy(step = PropensityStep.Preference) }
            PropensityStep.Result -> updateState { it.copy(step = PropensityStep.ValueConsumption) }
        }
    }

    fun nextStep() {
        when (currentState.step) {
            PropensityStep.Preference -> updateState { it.copy(step = PropensityStep.ValueConsumption) }
            PropensityStep.ValueConsumption -> viewModelScope.launch {
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
                            resultDescription = result.description,
                            step = PropensityStep.Result,
                        )
                    }
                }.onFailure { throwable ->
                    val message = when (throwable) {
                        is LbTripPropensityException.InvalidInputException -> "입력값을 다시 확인해 주세요."
                        else -> "진단 결과를 가져오지 못했어요. 잠시 후 다시 시도해 주세요."
                    }
                    updateState { it.copy(errorMessage = message) }
                }
                updateState { it.copy(isLoading = false) }
            }
            PropensityStep.Result -> Unit
        }
    }

    fun restartDiagnosis() {
        updateState {
            it.copy(
                step = PropensityStep.Preference,
                locality = 3, frugality = 3, experientiality = 3, vitality = 3, sociality = 3,
                accommodation = 3, food = 3, experience = 3, transportation = 3, cafeExhibition = 3,
                resultType = null, resultDescription = null,
            )
        }
    }

    fun onCourseRecommendationClicked() {
        updateState { it.copy(errorMessage = "코스 추천 기능은 준비 중이에요.") }
    }
}
