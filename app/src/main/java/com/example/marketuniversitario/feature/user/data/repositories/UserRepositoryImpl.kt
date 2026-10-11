package com.example.marketuniversitario.feature.user.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.marketuniversitario.feature.user.data.datasources.UserLocalDataSource
import com.example.marketuniversitario.feature.user.data.datasources.UserRemoteDataSource
import com.example.marketuniversitario.feature.user.data.models.toDomain
import com.example.marketuniversitario.feature.user.data.models.toRoomEntity
import com.example.marketuniversitario.feature.user.data.workers.SyncUserWorker
import com.example.marketuniversitario.feature.user.domain.model.User
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val roomEntity = user.toRoomEntity(isSynced = false)
            localDataSource.saveUser(roomEntity)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncUserWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_users_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User?> {
        return try {
            // 1. Intentar actualizar desde remoto con un timeout rápido (3 segundos)
            try {
                withTimeoutOrNull(3000.milliseconds) {
                    val remoteEntity = remoteDataSource.getUser(userId)
                    if (remoteEntity != null) {
                        localDataSource.saveUser(remoteEntity.toDomain().toRoomEntity(isSynced = true))
                    }
                }
            } catch (_: Exception) {
                // Ignorar error de red / timeout
            }

            // 2. Devolver desde la base de datos local
            val localEntity = localDataSource.getUserById(userId)
            Result.success(localEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserStream(userId: String): Flow<User?> {
        // Continuar usando RemoteDataSource temporalmente si se desea realtime, 
        // pero podemos emitir del Room si tuviéramos un getRoomStream().
        return remoteDataSource.getUserStream(userId).map { it?.toDomain() }
    }

    override suspend fun updateUserBusinessStatus(userId: String, hasBusiness: Boolean, businessId: String?): Result<Unit> {
        return try {
            // Actualización local rápida primero
            val localUser = localDataSource.getUserById(userId)
            if (localUser != null) {
                val updatedUser = localUser.copy(
                    hasBusiness = hasBusiness,
                    businessId = businessId,
                    isSynced = false
                )
                localDataSource.saveUser(updatedUser)
            }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncUserWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_users_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            val roomEntity = user.toRoomEntity(isSynced = false)
            localDataSource.saveUser(roomEntity)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncUserWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_users_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
