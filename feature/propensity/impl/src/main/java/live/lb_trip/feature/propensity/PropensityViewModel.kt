package live.lb_trip.feature.propensity

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
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
                postSideEffect(PropensitySideEffect.DiagnosisSubmitted)
                postSideEffect(PropensitySideEffect.NavigateToResult)
            }.onFailure { throwable ->
                if (throwable is LbTripPropensityException.UnauthenticatedException) {
                    updateState { it.copy(errorMessage = context.getString(R.string.propensity_error_unauthenticated)) }
                    postSideEffect(PropensitySideEffect.NavigateToSignin)
                } else {
                    val message = when (throwable) {
                        is LbTripPropensityException.InvalidInputException -> context.getString(R.string.propensity_error_invalid_input)
                        else -> context.getString(R.string.propensity_error_generic)
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
