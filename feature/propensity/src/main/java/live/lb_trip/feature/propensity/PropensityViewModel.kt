package live.lb_trip.feature.propensity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.ValueConsumption
import live.lb_trip.domain.usecase.SubmitPropensityUseCase

@HiltViewModel
class PropensityViewModel @Inject constructor(
    private val submitPropensityUseCase: SubmitPropensityUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropensityUiState())
    val uiState: StateFlow<PropensityUiState> = _uiState.asStateFlow()

    private val _effects = Channel<PropensityEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun updateLocality(value: Int) = _uiState.update { it.copy(locality = value) }
    fun updateFrugality(value: Int) = _uiState.update { it.copy(frugality = value) }
    fun updateExperientiality(value: Int) = _uiState.update { it.copy(experientiality = value) }
    fun updateVitality(value: Int) = _uiState.update { it.copy(vitality = value) }
    fun updateSociality(value: Int) = _uiState.update { it.copy(sociality = value) }
    fun updateAccommodation(value: Int) = _uiState.update { it.copy(accommodation = value) }
    fun updateFood(value: Int) = _uiState.update { it.copy(food = value) }
    fun updateExperience(value: Int) = _uiState.update { it.copy(experience = value) }
    fun updateTransportation(value: Int) = _uiState.update { it.copy(transportation = value) }
    fun updateCafeExhibition(value: Int) = _uiState.update { it.copy(cafeExhibition = value) }

    fun navigateBack() {
        when (_uiState.value.step) {
            PropensityStep.Preference -> viewModelScope.launch { _effects.send(PropensityEffect.NavigateBack) }
            PropensityStep.ValueConsumption -> _uiState.update { it.copy(step = PropensityStep.Preference) }
            PropensityStep.Result -> _uiState.update { it.copy(step = PropensityStep.ValueConsumption) }
        }
    }

    fun nextStep() {
        when (_uiState.value.step) {
            PropensityStep.Preference -> _uiState.update { it.copy(step = PropensityStep.ValueConsumption) }
            PropensityStep.ValueConsumption -> viewModelScope.launch {
                val current = _uiState.value
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                    _uiState.update {
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
                    _uiState.update { it.copy(errorMessage = message) }
                }
                _uiState.update { it.copy(isLoading = false) }
            }
            PropensityStep.Result -> Unit
        }
    }

    fun restartDiagnosis() {
        _uiState.update {
            it.copy(
                step = PropensityStep.Preference,
                locality = 3, frugality = 3, experientiality = 3, vitality = 3, sociality = 3,
                accommodation = 3, food = 3, experience = 3, transportation = 3, cafeExhibition = 3,
                resultType = null, resultDescription = null,
            )
        }
    }

    fun onCourseRecommendationClicked() {
        _uiState.update { it.copy(errorMessage = "코스 추천 기능은 준비 중이에요.") }
    }
}
