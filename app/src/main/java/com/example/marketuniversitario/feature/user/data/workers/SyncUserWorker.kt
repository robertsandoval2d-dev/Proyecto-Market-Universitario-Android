package com.example.marketuniversitario.feature.user.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.user.data.datasources.UserLocalDataSource
import com.example.marketuniversitario.feature.user.data.datasources.UserRemoteDataSource
import com.example.marketuniversitario.feature.user.data.models.toDomain
import com.example.marketuniversitario.feature.user.data.models.toEntity
import com.example.marketuniversitario.feature.user.domain.repositories.StorageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncUserWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource,
    private val storageRepository: StorageRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncUserWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedEntities = localDataSource.getUnsyncedUsers()
            Log.d(TAG, "Found ${unsyncedEntities.size} unsynced users to sync.")
            if (unsyncedEntities.isEmpty()) {
                return Result.success()
            }

            for (entity in unsyncedEntities) {
                val user = entity.toDomain()
                var photoUrl = user.photoUrl

                Log.d(TAG, "Syncing user: ${user.name} (ID: ${user.id})")

                // Si la imagen es local, subirla a Storage primero
                if (!photoUrl.isNullOrBlank() && !photoUrl.startsWith("http")) {
                    Log.d(TAG, "Uploading local photo image for user: $photoUrl")
                    val uploadResult = storageRepository.uploadProfileImage(user.id, photoUrl)
                    val downloadUrl = uploadResult.getOrNull()
                    if (downloadUrl != null) {
                        photoUrl = downloadUrl
                        Log.d(TAG, "Photo image uploaded successfully: $downloadUrl")
                    } else {
                        Log.e(TAG, "Failed to upload photo image for user ${user.id}: ${uploadResult.exceptionOrNull()?.message}")
                        return Result.retry()
                    }
                }

                val userToUpload = user.copy(photoUrl = photoUrl)

                // Subir a Firestore
                Log.d(TAG, "Saving user to Firestore: ${userToUpload.id}")
                remoteDataSource.saveUser(userToUpload.toEntity())

                // Marcar como sincronizado en local
                localDataSource.markAsSynced(user.id)
                Log.d(TAG, "User successfully synced and marked in Room: ${user.id}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Exception during user synchronization", e)
            if (runAttemptCount < 5) Result.retry() else Result.failure()
        }
    }
}
