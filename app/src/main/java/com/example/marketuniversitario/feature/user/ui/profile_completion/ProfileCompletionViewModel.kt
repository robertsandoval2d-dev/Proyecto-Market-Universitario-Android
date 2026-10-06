package com.example.marketuniversitario.feature.user.ui.profile_completion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import com.example.marketuniversitario.feature.user.domain.usecases.CompleteUserProfileUseCase
import com.example.marketuniversitario.feature.user.domain.usecases.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val completeUserProfileUseCase: CompleteUserProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileCompletionState(
            name = savedStateHandle.get<String>("name") ?: "",
            phone = savedStateHandle.get<String>("phone") ?: "",
            faculty = savedStateHandle.get<String>("faculty") ?: "",
            gender = savedStateHandle.get<String>("gender") ?: "",
            birthday = savedStateHandle.get<Long>("birthday"),
            photoUri = savedStateHandle.get<String>("photoUri"),
            selectedPreferences = savedStateHandle.get<List<String>>("selectedPreferences") ?: emptyList(),
            primaryIntent = savedStateHandle.get<String>("primaryIntent") ?: ""
        )
    )
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            // Cargar usuario
            userRepository.getUser(uid).onSuccess { user ->
                if (user != null) {
                    _state.update {
                        it.copy(
                            email = user.email,
                            name = user.name,
                            photoUri = user.photoUrl
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ProfileCompletionEvent) {
        when (event) {
            is ProfileCompletionEvent.PhotoSelected -> {
                savedStateHandle["photoUri"] = event.uri
                _state.update { it.copy(photoUri = event.uri) }
            }
            is ProfileCompletionEvent.NameChanged -> {
                savedStateHandle["name"] = event.name
                _state.update { it.copy(name = event.name, nameError = null) }
            }
            is ProfileCompletionEvent.PhoneChanged -> {
                savedStateHandle["phone"] = event.phone
                _state.update { it.copy(phone = event.phone, phoneError = null) }
            }
            is ProfileCompletionEvent.FacultySelected -> {
                savedStateHandle["faculty"] = event.faculty
                _state.update { it.copy(faculty = event.faculty, facultyError = null) }
            }
            is ProfileCompletionEvent.GenderSelected -> {
                savedStateHandle["gender"] = event.gender
                _state.update { it.copy(gender = event.gender) }
            }
            is ProfileCompletionEvent.BirthdaySelected -> {
                savedStateHandle["birthday"] = event.dateMillis
                _state.update { it.copy(birthday = event.dateMillis) }
            }

            is ProfileCompletionEvent.PreferenceToggled -> togglePreference(event.preference)
            is ProfileCompletionEvent.IntentSelected -> {
                savedStateHandle["primaryIntent"] = event.intent
                _state.update { it.copy(primaryIntent = event.intent) }
            }

            is ProfileCompletionEvent.SubmitProfile -> submitProfile()
            is ProfileCompletionEvent.DismissDialog -> _state.update { it.copy(status = ProfileCompletionStatus.Idle) }
            is ProfileCompletionEvent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.update { it.copy(status = ProfileCompletionStatus.LoggedOut) }
        }
    }

    fun validateStep1(): Boolean {
        val currentState = _state.value
        val nameError = if (currentState.name.isBlank()) "Campo obligatorio" else null
        val phoneError = if (currentState.phone.isBlank()) "Campo obligatorio" else null
        val facultyError = if (currentState.faculty.isBlank()) "Campo obligatorio" else null
        
        _state.update { it.copy(
            nameError = nameError,
            phoneError = phoneError,
            facultyError = facultyError
        )}
        
        return nameError == null && phoneError == null && facultyError == null
    }

    private fun togglePreference(preference: String) {
        val currentPreferences = _state.value.selectedPreferences.toMutableList()

        if (currentPreferences.contains(preference)) {
            currentPreferences.remove(preference)
        } else {
            if (currentPreferences.size < 3) { // Límite 3 opciones
                currentPreferences.add(preference)
            } else {
                _state.update { it.copy(status = ProfileCompletionStatus.Error("Solo puedes seleccionar hasta 3 preferencias")) }
                return
            }
        }
        savedStateHandle["selectedPreferences"] = currentPreferences
        _state.update { it.copy(selectedPreferences = currentPreferences) }
    }

    private fun submitProfile() {
        val currentState = _state.value
        val uid = authRepository.getCurrentUserId()

        if (uid == null) {
            _state.update { it.copy(status = ProfileCompletionStatus.Error("Sesión no encontrada")) }
            return
        }

        // Validación final
        if (currentState.name.isBlank() || currentState.faculty.isBlank() || currentState.phone.length != 9) {
            _state.update { it.copy(status = ProfileCompletionStatus.Error("Por favor, completa los campos obligatorios correctamente")) }
            return
        }

        _state.update { it.copy(status = ProfileCompletionStatus.Loading) }

        viewModelScope.launch {
            try {
                withTimeout(15_000L) {
                    var uploadedPhotoUrl: String? = null

                    // Subir a Storage
                    if (!currentState.photoUri.isNullOrBlank() && !currentState.photoUri.startsWith("http")) {
                        val uploadResult = uploadProfileImageUseCase(uid, currentState.photoUri)

                        // Si falla, actualizamos el estado y salimos del withTimeout de forma segura
                        uploadedPhotoUrl = uploadResult.getOrElse { error ->
                            _state.update { it.copy(status = ProfileCompletionStatus.Error("Error al subir imagen: ${error.message}")) }
                            return@withTimeout
                        }
                    } else {
                        uploadedPhotoUrl = currentState.photoUri
                    }

                    completeUserProfileUseCase(
                        userId = uid,
                        name = currentState.name,
                        phone = currentState.phone,
                        faculty = currentState.faculty,
                        gender = currentState.gender,
                        birthday = currentState.birthday,
                        preferences = currentState.selectedPreferences,
                        primaryIntent = currentState.primaryIntent,
                        photoUrl = uploadedPhotoUrl
                    ).onSuccess {
                        _state.update { it.copy(status = ProfileCompletionStatus.Success) }
                    }.onFailure { error ->
                        _state.update { it.copy(status = ProfileCompletionStatus.Error(error.message ?: "Error al guardar el perfil")) }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _state.update { it.copy(status = ProfileCompletionStatus.Error("Tiempo de espera agotado. Revisa tu conexión.")) }
            }
        }
    }
}