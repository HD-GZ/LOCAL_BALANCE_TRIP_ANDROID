package live.lb_trip.feature.propensity

sealed interface PropensityIntent {
    data class LocalityChanged(val value: Int) : PropensityIntent
    data class FrugalityChanged(val value: Int) : PropensityIntent
    data class ExperientialityChanged(val value: Int) : PropensityIntent
    data class VitalityChanged(val value: Int) : PropensityIntent
    data class SocialityChanged(val value: Int) : PropensityIntent
    data class AccommodationChanged(val value: Int) : PropensityIntent
    data class FoodChanged(val value: Int) : PropensityIntent
    data class ExperienceChanged(val value: Int) : PropensityIntent
    data class TransportationChanged(val value: Int) : PropensityIntent
    data class CafeExhibitionChanged(val value: Int) : PropensityIntent
    data object SubmitAndViewResult : PropensityIntent
    data object RestartDiagnosis : PropensityIntent
    data object CourseRecommendationClicked : PropensityIntent
}
