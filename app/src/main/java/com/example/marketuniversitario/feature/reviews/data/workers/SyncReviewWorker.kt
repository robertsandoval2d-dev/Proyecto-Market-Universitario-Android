package com.example.marketuniversitario.feature.reviews.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.reviews.data.datasources.ReviewLocalDataSource
import com.example.marketuniversitario.feature.reviews.data.datasources.ReviewRemoteDataSource
import com.example.marketuniversitario.feature.reviews.data.models.toDomain
import com.example.marketuniversitario.feature.reviews.data.models.toEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncReviewWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: ReviewLocalDataSource,
    private val remoteDataSource: ReviewRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncReviewWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedReviews = localDataSource.getUnsyncedReviews()
            if (unsyncedReviews.isEmpty()) return Result.success()

            for (roomEntity in unsyncedReviews) {
                Log.d(TAG, "Syncing review: ${roomEntity.id} for product ${roomEntity.productId}")
                val review = roomEntity.toDomain()
                remoteDataSource.saveReview(roomEntity.productId, review.toEntity())
                localDataSource.markAsSynced(roomEntity.id)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync reviews", e)
            Result.retry()
        }
    }
}
