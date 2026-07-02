package live.lb_trip.feature.propensity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import live.lb_trip.domain.exception.propensity.LbTripPropensityException
import live.lb_trip.domain.model.Preference
import live.lb_trip.domain.model.ValueConsumption
import live.lb_trip.domain.usecase.SubmitPropensityUseCase

class PropensityPresenter @AssistedInject constructor(
    @Assisted private val screen: PropensityScreen,
    @Assisted private val navigator: Navigator,
    private val submitPropensityUseCase: SubmitPropensityUseCase,
) : Presenter<PropensityState> {

    @AssistedFactory
    interface Factory {
        fun create(screen: PropensityScreen, navigator: Navigator): PropensityPresenter
    }

    @Composable
    override fun present(): PropensityState {
        var step by remember { mutableStateOf(PropensityStep.Preference) }
        var locality by remember { mutableIntStateOf(3) }
        var frugality by remember { mutableIntStateOf(3) }
        var experientiality by remember { mutableIntStateOf(3) }
        var vitality by remember { mutableIntStateOf(3) }
        var sociality by remember { mutableIntStateOf(3) }
        var accommodation by remember { mutableIntStateOf(3) }
        var food by remember { mutableIntStateOf(3) }
        var experience by remember { mutableIntStateOf(3) }
        var transportation by remember { mutableIntStateOf(3) }
        var cafeExhibition by remember { mutableIntStateOf(3) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var resultType by remember { mutableStateOf<String?>(null) }
        var resultDescription by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()
        val invalidInputErrorMessage = stringResource(R.string.propensity_error_invalid_input)
        val genericErrorMessage = stringResource(R.string.propensity_error_generic)
        val courseComingSoonMessage = stringResource(R.string.propensity_course_coming_soon)

        return PropensityState(
            step = step,
            locality = locality,
            frugality = frugality,
            experientiality = experientiality,
            vitality = vitality,
            sociality = sociality,
            accommodation = accommodation,
            food = food,
            experience = experience,
            transportation = transportation,
            cafeExhibition = cafeExhibition,
            isLoading = isLoading,
            errorMessage = errorMessage,
            resultType = resultType,
            resultDescription = resultDescription,
        ) { event ->
            when (event) {
                PropensityEvent.NavigateBack -> when (step) {
                    PropensityStep.Preference -> navigator.pop()
                    PropensityStep.ValueConsumption -> step = PropensityStep.Preference
                    PropensityStep.Result -> step = PropensityStep.ValueConsumption
                }

                is PropensityEvent.UpdateLocality -> locality = event.value
                is PropensityEvent.UpdateFrugality -> frugality = event.value
                is PropensityEvent.UpdateExperientiality -> experientiality = event.value
                is PropensityEvent.UpdateVitality -> vitality = event.value
                is PropensityEvent.UpdateSociality -> sociality = event.value

                is PropensityEvent.UpdateAccommodation -> accommodation = event.value
                is PropensityEvent.UpdateFood -> food = event.value
                is PropensityEvent.UpdateExperience -> experience = event.value
                is PropensityEvent.UpdateTransportation -> transportation = event.value
                is PropensityEvent.UpdateCafeExhibition -> cafeExhibition = event.value

                PropensityEvent.NextStep -> when (step) {
                    PropensityStep.Preference -> step = PropensityStep.ValueConsumption
                    PropensityStep.ValueConsumption -> scope.launch {
                        isLoading = true
                        errorMessage = null
                        submitPropensityUseCase(
                            preference = Preference(
                                locality = locality,
                                frugality = frugality,
                                experientiality = experientiality,
                                vitality = vitality,
                                sociality = sociality,
                            ),
                            valueConsumption = ValueConsumption(
                                accommodation = accommodation,
                                food = food,
                                experience = experience,
                                transportation = transportation,
                                cafeExhibition = cafeExhibition,
                            ),
                        ).onSuccess { result ->
                            resultType = result.type
                            resultDescription = result.description
                            step = PropensityStep.Result
                        }.onFailure { throwable ->
                            errorMessage = when (throwable) {
                                is LbTripPropensityException.InvalidInputException -> invalidInputErrorMessage
                                else -> genericErrorMessage
                            }
                        }
                        isLoading = false
                    }
                    PropensityStep.Result -> Unit
                }

                PropensityEvent.RestartDiagnosis -> {
                    step = PropensityStep.Preference
                    locality = 3
                    frugality = 3
                    experientiality = 3
                    vitality = 3
                    sociality = 3
                    accommodation = 3
                    food = 3
                    experience = 3
                    transportation = 3
                    cafeExhibition = 3
                    resultType = null
                    resultDescription = null
                }

                PropensityEvent.CourseRecommendationClicked ->
                    errorMessage = courseComingSoonMessage
            }
        }
    }
}
