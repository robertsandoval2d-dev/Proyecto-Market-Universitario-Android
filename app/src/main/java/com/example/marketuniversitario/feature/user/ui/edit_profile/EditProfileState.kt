package com.example.marketuniversitario.feature.user.ui.edit_profile

import com.example.marketuniversitario.feature.user.domain.model.User

data class EditProfileState(
    val user: User? = null,
    val photoUri: String? = null,
    val name: String = "",
    val phone: String = "",
    val faculty: String = "",
    val gender: String = "",
    val birthday: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

sealed interface EditProfileEvent {
    data class PhotoSelected(val uri: String) : EditProfileEvent
    data class NameChanged(val name: String) : EditProfileEvent
    data class PhoneChanged(val phone: String) : EditProfileEvent
    data class FacultyChanged(val faculty: String) : EditProfileEvent
    data class GenderChanged(val gender: String) : EditProfileEvent
    data class BirthdayChanged(val dateMillis: Long) : EditProfileEvent
    data object SaveProfile : EditProfileEvent
    data object ResetState : EditProfileEvent
}