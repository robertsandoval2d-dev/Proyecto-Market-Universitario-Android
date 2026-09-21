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
    val createdAt: Long = System.currentTimeMillis()
)

fun User.toEntity() = UserEntity(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    preferences = this.preferences,
    hasBusiness = this.hasBusiness,
    businessId = this.businessId
)

fun UserEntity.toDomain() = User(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    preferences = this.preferences,
    hasBusiness = this.hasBusiness,
    businessId = this.businessId
)