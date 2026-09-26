package com.example.marketuniversitario.feature.user.ui.profile_completion

sealed interface ProfileCompletionStatus {
    object Idle : ProfileCompletionStatus
    object Loading : ProfileCompletionStatus
    object Success : ProfileCompletionStatus
    object LoggedOut : ProfileCompletionStatus
    data class Error(val message: String) : ProfileCompletionStatus
}

data class ProfileCompletionState(
    // Datos
    val photoUri: String? = null,
    val name: String = "",
    val email: String = "", // solo lectura
    val phone: String = "",
    val faculty: String = "",
    val gender: String = "",
    val birthday: Long? = null,
    
    // Errores de validación
    val nameError: String? = null,
    val phoneError: String? = null,
    val facultyError: String? = null,

    // Preferencias
    val selectedPreferences: List<String> = emptyList(),
    val primaryIntent: String = "",

    val status: ProfileCompletionStatus = ProfileCompletionStatus.Idle
)

sealed interface ProfileCompletionEvent {
    // Eventos
    data class PhotoSelected(val uri: String) : ProfileCompletionEvent
    data class NameChanged(val name: String) : ProfileCompletionEvent
    data class PhoneChanged(val phone: String) : ProfileCompletionEvent
    data class FacultySelected(val faculty: String) : ProfileCompletionEvent
    data class GenderSelected(val gender: String) : ProfileCompletionEvent
    data class BirthdaySelected(val dateMillis: Long) : ProfileCompletionEvent

    data class PreferenceToggled(val preference: String) : ProfileCompletionEvent
    data class IntentSelected(val intent: String) : ProfileCompletionEvent

    // Acciones
    object SubmitProfile : ProfileCompletionEvent
    object DismissDialog : ProfileCompletionEvent
    object Logout : ProfileCompletionEvent
}