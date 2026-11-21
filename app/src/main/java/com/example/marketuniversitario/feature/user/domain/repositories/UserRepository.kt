package com.example.marketuniversitario.feature.user.domain.repositories

import com.example.marketuniversitario.feature.user.domain.model.AccountStatus
import com.example.marketuniversitario.feature.user.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(userId: String): Result<User?>
    suspend fun getUserRemote(userId: String): Result<User?>
    fun getUserStream(userId: String): Flow<User?>
    suspend fun updateUserBusinessStatus(userId: String, hasBusiness: Boolean, businessId: String?): Result<Unit>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun addRecentSearch(userId: String, searchQuery: String): Result<Unit>
    fun getAllUsers(): Flow<List<User>>

    /**
     * Aplica una suspensión temporal o un baneo permanente.
     * - Si es BANNED: suspendedUntil debe ir en null.
     * - Si es SUSPENDED: suspendedUntil contiene el timestamp futuro en ms.
     */
    suspend fun applyUserSanction(
        userId: String,
        status: AccountStatus,
        suspendedUntil: Long?,
        reason: String
    ): Result<Unit>

    /**
     * Levanta cualquier sanción (suspensión o baneo) y restaura la cuenta.
     * También se usa cuando la app detecta que una suspensión temporal ya expiró.
     */
    suspend fun restoreUserAccount(userId: String): Result<Unit>
}
