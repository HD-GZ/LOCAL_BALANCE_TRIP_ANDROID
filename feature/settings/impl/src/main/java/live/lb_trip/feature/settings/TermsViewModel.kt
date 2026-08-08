package live.lb_trip.feature.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.model.TermsType
import live.lb_trip.domain.usecase.GetTermsUseCase
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor(
    private val getTermsUseCase: GetTermsUseCase,
) : BaseViewModel<TermsUiState, TermsIntent, TermsSideEffect>(TermsUiState()) {

    private var loadedType: TermsType? = null

    override fun onIntent(intent: TermsIntent) {
        when (intent) {
            is TermsIntent.Load -> {
                if (loadedType == intent.type) return
                loadedType = intent.type
                viewModelScope.launch { load(intent.type) }
            }
            TermsIntent.Retry -> loadedType?.let { type -> viewModelScope.launch { load(type) } }
        }
    }

    private suspend fun load(type: TermsType) {
        updateState { it.copy(isLoading = true) }
        getTermsUseCase(type)
            .onSuccess { terms ->
                updateState {
                    it.copy(
                        isLoading = false,
                        title = terms.title,
                        version = terms.version,
                        effectiveDate = terms.effectiveDate.replace("-", "."),
                        blocks = parseTermsContent(terms.content).toPersistentList(),
                    )
                }
            }
            .onFailure {
                updateState { it.copy(isLoading = false) }
                postSideEffect(TermsSideEffect.ShowLoadError)
            }
    }
}

private fun parseTermsContent(content: String): List<TermsBlock> {
    val rawBlocks = content.split(Regex("\n\\s*\n")).map { it.trim() }.filter { it.isNotEmpty() }
    return rawBlocks.flatMap { block ->
        val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
        when {
            lines.isNotEmpty() && lines.all { it.startsWith("- ") } -> {
                val items = lines.map { it.removePrefix("- ").trim() }.toPersistentList()
                listOf(TermsBlock.BulletList(items))
            }
            lines.firstOrNull()?.startsWith("## ") == true -> {
                val heading = TermsBlock.Heading(lines.first().removePrefix("## ").trim())
                val rest = lines.drop(1).joinToString("\n")
                if (rest.isBlank()) listOf(heading) else listOf(heading, TermsBlock.Paragraph(rest))
            }
            else -> listOf(TermsBlock.Paragraph(lines.joinToString("\n")))
        }
    }
}
