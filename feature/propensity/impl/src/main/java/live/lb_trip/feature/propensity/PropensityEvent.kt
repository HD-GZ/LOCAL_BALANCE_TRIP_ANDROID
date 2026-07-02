package live.lb_trip.feature.propensity

import com.slack.circuit.runtime.CircuitUiEvent

sealed interface PropensityEvent : CircuitUiEvent {
    data object NavigateBack : PropensityEvent

    data class UpdateLocality(val value: Int) : PropensityEvent
    data class UpdateFrugality(val value: Int) : PropensityEvent
    data class UpdateExperientiality(val value: Int) : PropensityEvent
    data class UpdateVitality(val value: Int) : PropensityEvent
    data class UpdateSociality(val value: Int) : PropensityEvent

    data class UpdateAccommodation(val value: Int) : PropensityEvent
    data class UpdateFood(val value: Int) : PropensityEvent
    data class UpdateExperience(val value: Int) : PropensityEvent
    data class UpdateTransportation(val value: Int) : PropensityEvent
    data class UpdateCafeExhibition(val value: Int) : PropensityEvent

    data object NextStep : PropensityEvent
    data object RestartDiagnosis : PropensityEvent
    data object CourseRecommendationClicked : PropensityEvent
}
