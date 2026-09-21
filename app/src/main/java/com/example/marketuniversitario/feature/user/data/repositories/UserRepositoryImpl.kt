package com.example.marketuniversitario.feature.user.data.repositories

import com.example.marketuniversitario.feature.user.data.datasources.UserRemoteDataSource
import com.example.marketuniversitario.feature.user.data.models.toDomain
import com.example.marketuniversitario.feature.user.data.models.toEntity
import com.example.marketuniversitario.feature.user.domain.model.User
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val entity = user.toEntity()

            remoteDataSource.saveUser(entity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User?> {
        return try {
            val entity = remoteDataSource.getUser(userId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}