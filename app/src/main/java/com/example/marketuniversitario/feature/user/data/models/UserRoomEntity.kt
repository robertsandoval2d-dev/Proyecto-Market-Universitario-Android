package com.example.marketuniversitario.feature.user.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.user.domain.model.User
import java.util.UUID

@Entity(tableName = "users")
data class UserRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val preferences: String, // Comma-separated list of preferences
    val hasBusiness: Boolean,
    val businessId: String?,
    val photoUrl: String?,
    val birthday: Long?,
    val faculty: String?,
    val gender: String?,
    val primaryIntent: String?,
    val isProfileComplete: Boolean,
    val isSynced: Boolean = false // Para sincronización offline con WorkManager
)

fun User.toRoomEntity(isSynced: Boolean = false) = UserRoomEntity(
    id = this.id.ifBlank { UUID.randomUUID().toString() },
    name = this.name,
    email = this.email,
    phone = this.phone,
    preferences = this.preferences.joinToString(","),
    hasBusiness = this.hasBusiness,
    businessId = this.businessId,
    photoUrl = this.photoUrl,
    birthday = this.birthday,
    faculty = this.faculty,
    gender = this.gender,
    primaryIntent = this.primaryIntent,
    isProfileComplete = this.isProfileComplete,
    isSynced = isSynced
)

fun UserRoomEntity.toDomain() = User(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    preferences = if (this.preferences.isBlank()) emptyList() else this.preferences.split(","),
    hasBusiness = this.hasBusiness,
    businessId = this.businessId,
    photoUrl = this.photoUrl,
    birthday = this.birthday,
    faculty = this.faculty,
    gender = this.gender,
    primaryIntent = this.primaryIntent,
    isProfileComplete = this.isProfileComplete
)
