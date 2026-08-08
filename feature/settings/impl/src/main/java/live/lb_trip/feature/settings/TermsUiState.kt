package live.lb_trip.feature.settings

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class TermsUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val version: String = "",
    val effectiveDate: String = "",
    val blocks: ImmutableList<TermsBlock> = persistentListOf(),
)

sealed interface TermsBlock {
    data class Heading(val text: String) : TermsBlock
    data class Paragraph(val text: String) : TermsBlock
    data class BulletList(val items: ImmutableList<String>) : TermsBlock
}

sealed interface TermsSideEffect {
    data object ShowLoadError : TermsSideEffect
}
