package com.example.marketuniversitario.feature.profile.ui.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import com.example.marketuniversitario.feature.user.domain.usecases.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

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
            is EditProfileEvent.ResetState -> _state.update { it.copy(isSuccess = false, error = null) }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            userRepository.getUser(uid).onSuccess { user ->
                if (user != null) {
                    _state.update {
                        it.copy(
                            user = user,
                            photoUri = user.photoUrl,
                            name = user.name,
                            phone = user.phone,
                            faculty = user.faculty ?: "",
                            gender = user.gender ?: "",
                            birthday = user.birthday
                        )
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        val currentState = _state.value
        val user = currentState.user ?: return
        val uid = authRepository.getCurrentUserId() ?: return
        
        if (currentState.name.isBlank() || currentState.faculty.isBlank() || currentState.phone.length != 9) return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                withTimeout(15_000L) {
                    var uploadedPhotoUrl: String? = null

                    if (!currentState.photoUri.isNullOrBlank() && !currentState.photoUri.startsWith("http")) {
                        val uploadResult = uploadProfileImageUseCase(uid, currentState.photoUri)
                        uploadedPhotoUrl = uploadResult.getOrElse { error ->
                            _state.update { it.copy(isLoading = false, error = "Error al subir imagen: ${error.message}") }
                            return@withTimeout
                        }
                    } else {
                        uploadedPhotoUrl = currentState.photoUri
                    }

                    val updatedUser = user.copy(
                        name = currentState.name,
                        phone = currentState.phone,
                        faculty = currentState.faculty,
                        gender = currentState.gender,
                        birthday = currentState.birthday,
                        photoUrl = uploadedPhotoUrl
                    )

                    userRepository.updateUserProfile(updatedUser).onSuccess {
                        _state.update { it.copy(isLoading = false, isSuccess = true) }
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false, error = error.message ?: "Error al guardar el perfil") }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _state.update { it.copy(isLoading = false, error = "Tiempo de espera agotado. Revisa tu conexión.") }
            }
        }
    }
}