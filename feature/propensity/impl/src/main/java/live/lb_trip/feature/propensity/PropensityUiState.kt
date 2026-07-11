package live.lb_trip.feature.propensity

enum class PropensityStep { Preference, ValueConsumption, Result }

data class PropensityUiState(
    val step: PropensityStep = PropensityStep.Preference,
    val locality: Int = 3,
    val frugality: Int = 3,
    val experientiality: Int = 3,
    val vitality: Int = 3,
    val sociality: Int = 3,
    val accommodation: Int = 3,
    val food: Int = 3,
    val experience: Int = 3,
    val transportation: Int = 3,
    val cafeExhibition: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resultType: String? = null,
    val resultDescription: String? = null,
)

sealed interface PropensitySideEffect {
    data object NavigateBack : PropensitySideEffect
    data object NavigateToRecommendation : PropensitySideEffect
}
