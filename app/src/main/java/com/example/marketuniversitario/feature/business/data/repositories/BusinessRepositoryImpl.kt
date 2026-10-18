package com.example.marketuniversitario.feature.business.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.marketuniversitario.feature.business.data.datasources.BusinessLocalDataSource
import com.example.marketuniversitario.feature.business.data.datasources.BusinessRemoteDataSource
import com.example.marketuniversitario.feature.business.data.models.toDomain
import com.example.marketuniversitario.feature.business.data.models.toRoomEntity
import com.example.marketuniversitario.feature.business.data.workers.SyncBusinessWorker
import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class BusinessRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localDataSource: BusinessLocalDataSource,
    private val remoteDataSource: BusinessRemoteDataSource
) : BusinessRepository {

    override suspend fun createBusiness(business: Business): Result<Business> {
        return try {
            // Guardar localmente primero (Offline-First)
            val roomEntity = business.toRoomEntity(isSynced = false)
            localDataSource.saveBusiness(roomEntity)
            
            // Retornar entidad de dominio al instante
            val businessResult = roomEntity.toDomain()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncBusinessWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_businesses_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(businessResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBusiness(businessId: String): Result<Business?> {
        return try {
            // 1. Intentar actualizar desde remoto con un timeout rápido (3 segundos) si hay red
            val remoteEntity = try {
                withTimeoutOrNull(3000.milliseconds) {
                    remoteDataSource.getBusiness(businessId)
                }
            } catch (_: Exception) {
                null // Ignorar error de red / timeout
            }

            if (remoteEntity != null) {
                val localEntity = localDataSource.getBusinessById(businessId)
                if (localEntity == null || localEntity.isSynced) {
                    localDataSource.saveBusiness(remoteEntity.toDomain().toRoomEntity(isSynced = true))
                }
            }

            // 2. Devolver desde la base de datos local (Room)
            val localEntity = localDataSource.getBusinessById(businessId)
            Result.success(localEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPublicBusinessInfo(businessId: String): Result<Business?> {
        return try {
            val remoteEntity = remoteDataSource.getBusiness(businessId)
            Result.success(remoteEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBusiness(business: Business): Result<Unit> {
        return try {
            val roomEntity = business.toRoomEntity(isSynced = false)
            localDataSource.saveBusiness(roomEntity)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncBusinessWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_businesses_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
