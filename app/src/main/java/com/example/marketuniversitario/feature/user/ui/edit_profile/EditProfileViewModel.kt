package com.example.marketuniversitario.feature.user.ui.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.usecases.GetUserProfileInfoUseCase
import com.example.marketuniversitario.feature.user.domain.usecases.UpdateProfileInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserProfileInfoUseCase: GetUserProfileInfoUseCase,
    private val updateProfileInfoUseCase: UpdateProfileInfoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    private val _effects = Channel<EditProfileEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadUser()
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.PhotoSelected -> _state.update { it.copy(photoUri = event.uri) }
            is EditProfileEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is EditProfileEvent.PhoneChanged -> _state.update { it.copy(phone = event.phone) }
            is EditProfileEvent.FacultyChanged -> _state.update { it.copy(faculty = event.faculty) }
            is EditProfileEvent.GenderChanged -> _state.update { it.copy(gender = event.gender) }
            is EditProfileEvent.BirthdayChanged -> _state.update { it.copy(birthday = event.dateMillis) }
            is EditProfileEvent.SaveProfile -> saveProfile()
            is EditProfileEvent.DismissError -> _state.update { it.copy(saveStatus = SaveStatus.Idle) }
            is EditProfileEvent.Retry -> loadUser()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _state.update { it.copy( status = EditProfileStatus.Loading) }
            getUserProfileInfoUseCase()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user,
                            photoUri = user.photoUrl,
                            name = user.name,
                            phone = user.phone,
                            faculty = user.faculty ?: "",
                            gender = user.gender ?: "",
                            birthday = user.birthday,
                            status = EditProfileStatus.Success
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(status = EditProfileStatus.Error(error.message ?: "Error al cargar el perfil"))
                    }
                }
        }
    }

    private fun saveProfile() {
        val currentState = _state.value
        val currentUser = currentState.user ?: return
        if (currentState.saveStatus is SaveStatus.Saving) return

        viewModelScope.launch {
            _state.update { it.copy(saveStatus = SaveStatus.Saving) }
            try {
                withTimeout(15.seconds) {

                    val userToSave = currentUser.copy(
                        photoUrl = currentState.photoUri,
                        name = currentState.name,
                        phone = currentState.phone,
                        faculty = currentState.faculty,
                        gender = currentState.gender,
                        birthday = currentState.birthday
                    )

                    updateProfileInfoUseCase(userToSave).fold(
                        onSuccess = {
                            _state.update { it.copy(saveStatus = SaveStatus.Idle) }
                            _effects.send(EditProfileEffect.Saved) },
                        onFailure = { e ->
                            _state.update {
                                it.copy(saveStatus = SaveStatus.Error(e.message ?: "Error al guardar el perfil"))
                            }
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                _state.update { it.copy(saveStatus = SaveStatus.Error("Tiempo de espera agotado. Revisa tu conexión.")) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(saveStatus = SaveStatus.Error(e.message ?: "Error inesperado")) }
            }
        }
    }
}
