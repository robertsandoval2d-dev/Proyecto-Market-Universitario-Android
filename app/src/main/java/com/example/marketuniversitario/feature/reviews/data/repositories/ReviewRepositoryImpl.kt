package com.example.marketuniversitario.feature.reviews.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.marketuniversitario.feature.reviews.data.datasources.ReviewLocalDataSource
import com.example.marketuniversitario.feature.reviews.data.datasources.ReviewRemoteDataSource
import com.example.marketuniversitario.feature.reviews.data.models.toDomain
import com.example.marketuniversitario.feature.reviews.data.models.toEntity
import com.example.marketuniversitario.feature.reviews.data.models.toRoomEntity
import com.example.marketuniversitario.feature.reviews.data.workers.SyncReviewWorker
import com.example.marketuniversitario.feature.reviews.domain.models.Review
import com.example.marketuniversitario.feature.reviews.domain.repositories.ReviewRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val remoteDataSource: ReviewRemoteDataSource,
    private val localDataSource: ReviewLocalDataSource
) : ReviewRepository {

    override fun getReviews(productId: String): Flow<List<Review>> {
        // Sincronizar remotamente a SQLite en segundo plano si hay conexión
        CoroutineScope(Dispatchers.IO).launch {
            try {
                remoteDataSource.getReviews(productId).collect { entities ->
                    val roomEntities = entities.map { it.toDomain().toRoomEntity(productId, isSynced = true) }
                    localDataSource.insertReviews(roomEntities)
                }
            } catch (_: Exception) {}
        }
        // Consultar siempre desde SQLite local (Offline-first)
        return localDataSource.getReviews(productId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReview(productId: String, studentId: String): Result<Review?> {
        return try {
            // 1. Intentar consultar local primero
            val localEntity = localDataSource.getReviewByUser(productId, studentId)
            if (localEntity != null) {
                return Result.success(localEntity.toDomain())
            }

            // 2. Si no está en local, intentar remoto
            val remoteEntity = remoteDataSource.getReview(productId, studentId)
            if (remoteEntity != null) {
                localDataSource.insertReview(remoteEntity.toDomain().toRoomEntity(productId, isSynced = true))
                return Result.success(remoteEntity.toDomain())
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveReview(productId: String, review: Review): Result<Unit> {
        return try {
            // 1. Guardar localmente primero
            val roomEntity = review.toRoomEntity(productId, isSynced = false)
            localDataSource.insertReview(roomEntity)

            // 2. Programar WorkManager para sincronizar en segundo plano cuando haya conexión
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncReviewWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_reviews_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
