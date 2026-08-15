package live.lb_trip.feature.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import live.lb_trip.core.util.sharedInvalidFieldMessage
import live.lb_trip.core.viewmodel.BaseViewModel
import live.lb_trip.domain.exception.user.LbTripUserException
import live.lb_trip.domain.usecase.ClearDistanceRecordingUseCase
import live.lb_trip.domain.usecase.ClearSessionUseCase
import live.lb_trip.domain.usecase.GetUserProfileUseCase
import live.lb_trip.domain.usecase.UpdateUserProfileUseCase
import live.lb_trip.domain.usecase.WithdrawUserUseCase
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val withdrawUserUseCase: WithdrawUserUseCase,
    private val clearSessionUseCase: ClearSessionUseCase,
    private val clearDistanceRecordingUseCase: ClearDistanceRecordingUseCase,
) : BaseViewModel<EditProfileUiState, EditProfileIntent, EditProfileSideEffect>(EditProfileUiState()) {

    init {
        viewModelScope.launch { load() }
    }

    override fun onIntent(intent: EditProfileIntent) {
        when (intent) {
            is EditProfileIntent.NameChanged -> updateState { it.copy(name = intent.value) }
            is EditProfileIntent.BirthYearChanged -> updateState { it.copy(birthYear = intent.value) }
            is EditProfileIntent.BirthMonthChanged -> updateState { it.copy(birthMonth = intent.value) }
            is EditProfileIntent.BirthDayChanged -> updateState { it.copy(birthDay = intent.value) }
            is EditProfileIntent.GenderChanged -> updateState { it.copy(gender = intent.value) }
            is EditProfileIntent.PasswordChanged -> updateState { it.copy(password = intent.value) }
            is EditProfileIntent.PasswordConfirmChanged -> updateState { it.copy(passwordConfirm = intent.value) }
            EditProfileIntent.TogglePasswordVisibility ->
                updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            EditProfileIntent.ToggleConfirmPasswordVisibility ->
                updateState { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            EditProfileIntent.SaveClicked -> viewModelScope.launch { save() }
            EditProfileIntent.WithdrawClicked -> viewModelScope.launch { withdraw() }
        }
    }

    private suspend fun load() {
        updateState { it.copy(isLoading = true) }
        getUserProfileUseCase()
            .onSuccess { profile ->
                val (year, month, day) = parseBirthDate(profile.birthDate)
                updateState {
                    it.copy(
                        isLoading = false,
                        email = profile.email,
                        name = profile.name,
                        birthYear = year,
                        birthMonth = month,
                        birthDay = day,
                        gender = profile.gender,
                    )
                }
            }.onFailure {
                updateState { it.copy(isLoading = false) }
                postSideEffect(EditProfileSideEffect.ShowLoadError)
            }
    }

    private suspend fun save() {
        val current = currentState
        updateState { it.copy(isSaving = true) }
        val birthDate = "%04d-%02d-%02d".format(
            current.birthYear.toIntOrNull() ?: 0,
            current.birthMonth,
            current.birthDay.toIntOrNull() ?: 0,
        )
        updateUserProfileUseCase(
            name = current.name,
            birthDate = birthDate,
            gender = current.gender,
            password = current.password.ifEmpty { null },
            passwordConfirm = current.passwordConfirm.ifEmpty { null },
        ).onSuccess {
            updateState { it.copy(isSaving = false) }
            postSideEffect(EditProfileSideEffect.SaveSuccess)
        }.onFailure { throwable ->
            updateState { it.copy(isSaving = false) }
            postSideEffect(EditProfileSideEffect.ShowSaveError(saveFailureMessage(throwable)))
        }
    }

    private suspend fun withdraw() {
        updateState { it.copy(isWithdrawing = true) }
        withdrawUserUseCase()
            .onSuccess {
                withContext(NonCancellable) {
                    clearDistanceRecordingUseCase()
                    clearSessionUseCase()
                }
            }.onFailure { throwable ->
                updateState { it.copy(isWithdrawing = false) }
                postSideEffect(EditProfileSideEffect.ShowWithdrawError(withdrawFailureMessage(throwable)))
            }
    }

    private fun parseBirthDate(birthDate: String): Triple<String, Int, String> {
        val parts = birthDate.split("-")
        val year = parts.getOrNull(0).orEmpty()
        val month = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val day = parts.getOrNull(2)?.toIntOrNull()?.toString().orEmpty()
        return Triple(year, month, day)
    }

    private fun saveFailureMessage(throwable: Throwable): String = when (throwable) {
        is LbTripUserException.InvalidInputValueException ->
            throwable.fields.firstNotNullOfOrNull { field -> sharedInvalidFieldMessage(context, field) }
                ?: context.getString(R.string.edit_profile_error_invalid_input)
        else -> context.getString(R.string.edit_profile_error_save_failed)
    }

    private fun withdrawFailureMessage(throwable: Throwable): String = when (throwable) {
        is LbTripUserException.UserWithdrawnException -> context.getString(R.string.edit_profile_error_already_withdrawn)
        is LbTripUserException.UserNotFoundException -> context.getString(R.string.edit_profile_error_user_not_found)
        else -> context.getString(R.string.edit_profile_error_withdraw_failed)
    }
}
