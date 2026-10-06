package com.example.marketuniversitario.feature.user.domain.repositories

import com.example.marketuniversitario.feature.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User?>
    fun getUserStream(userId: String): Flow<User?>
    suspend fun updateUserBusinessStatus(userId: String, hasBusiness: Boolean, businessId: String?): Result<Unit>
    suspend fun updateUserProfile(user: User): Result<Unit>
}
