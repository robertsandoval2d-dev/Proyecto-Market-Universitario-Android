package com.example.marketuniversitario.feature.user.data.models

import com.example.marketuniversitario.feature.user.domain.model.User
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class UserEntity(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val preferences: List<String> = emptyList(),
    @get:PropertyName("hasBusiness") @set:PropertyName("hasBusiness")
    var hasBusiness: Boolean = false,
    val businessId: String? = null,
    val photoUrl: String? = null,
    val birthday: Long? = null,
    val faculty: String? = null,
    val gender: String? = null,
    val primaryIntent: String? = null,
    @get:PropertyName("isProfileComplete") @set:PropertyName("isProfileComplete")
    var isProfileComplete: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)

fun User.toEntity() = UserEntity(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    preferences = this.preferences,
    hasBusiness = this.hasBusiness,
    businessId = this.businessId,
    photoUrl = this.photoUrl,
    birthday = this.birthday,
    faculty = this.faculty,
    gender = this.gender,
    primaryIntent = this.primaryIntent,
    isProfileComplete = this.isProfileComplete
)

fun UserEntity.toDomain() = User(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    preferences = this.preferences,
    hasBusiness = this.hasBusiness,
    businessId = this.businessId,
    photoUrl = this.photoUrl,
    birthday = this.birthday,
    faculty = this.faculty,
    gender = this.gender,
    primaryIntent = this.primaryIntent,
    isProfileComplete = this.isProfileComplete
)