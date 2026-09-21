package com.example.marketuniversitario.feature.user.domain.repositories

import com.example.marketuniversitario.feature.user.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User?>
}