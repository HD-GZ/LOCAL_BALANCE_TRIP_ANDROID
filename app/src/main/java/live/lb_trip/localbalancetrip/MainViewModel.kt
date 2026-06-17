package live.lb_trip.localbalancetrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import live.lb_trip.domain.usecase.GetTokensUseCase

@HiltViewModel
class MainViewModel @Inject constructor(
    getTokensUseCase: GetTokensUseCase,
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean?> = getTokensUseCase()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)
}
