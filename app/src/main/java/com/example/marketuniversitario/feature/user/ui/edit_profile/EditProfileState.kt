package com.example.marketuniversitario.feature.user.ui.edit_profile

import com.example.marketuniversitario.feature.user.domain.model.User

sealed interface EditProfileStatus {
    object Loading : EditProfileStatus
    object Success : EditProfileStatus
    data class Error(val message: String) : EditProfileStatus
}

sealed interface SaveStatus {
    data object Idle : SaveStatus
    data object Saving : SaveStatus
    data class Error(val message: String) : SaveStatus
}

data class EditProfileState(
    val user: User? = null,
    val photoUri: String? = null,
    val name: String = "",
    val phone: String = "",
    val faculty: String = "",
    val gender: String = "",
    val birthday: Long? = null,

    val saveStatus: SaveStatus = SaveStatus.Idle,
    val status: EditProfileStatus = EditProfileStatus.Loading,
){
    val isFormValid: Boolean
        get() = name.isNotBlank() && faculty.isNotBlank() && phone.length == 9
}

sealed interface EditProfileEvent {
    data class PhotoSelected(val uri: String) : EditProfileEvent
    data class NameChanged(val name: String) : EditProfileEvent
    data class PhoneChanged(val phone: String) : EditProfileEvent
    data class FacultyChanged(val faculty: String) : EditProfileEvent
    data class GenderChanged(val gender: String) : EditProfileEvent
    data class BirthdayChanged(val dateMillis: Long) : EditProfileEvent
    data object SaveProfile : EditProfileEvent
    data object DismissError : EditProfileEvent
    data object Retry : EditProfileEvent
}

sealed interface EditProfileEffect {
    data object Saved : EditProfileEffect
}