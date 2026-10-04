package com.example.marketuniversitario.feature.business.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.business.data.datasources.ProductLocalDataSource
import com.example.marketuniversitario.feature.business.data.datasources.ProductRemoteDataSource
import com.example.marketuniversitario.feature.business.data.models.toDomain
import com.example.marketuniversitario.feature.business.data.models.toEntity
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessStorageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncProductWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: ProductLocalDataSource,
    private val remoteDataSource: ProductRemoteDataSource,
    private val storageRepository: BusinessStorageRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncProductWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedEntities = localDataSource.getUnsyncedProducts()
            Log.d(TAG, "Found ${unsyncedEntities.size} unsynced products to sync.")
            if (unsyncedEntities.isEmpty()) {
                return Result.success()
            }

            for (entity in unsyncedEntities) {
                val product = entity.toDomain()
                var images = product.images

                Log.d(TAG, "Syncing product: ${product.name} (ID: ${product.id})")

                // Si la imagen es local, subirla a Storage primero
                val localImageUri = images.firstOrNull()
                if (!localImageUri.isNullOrBlank() && !localImageUri.startsWith("http")) {
                    Log.d(TAG, "Uploading local image for product: $localImageUri")
                    val uploadResult = storageRepository.uploadProductImage(product.id, localImageUri)
                    val downloadUrl = uploadResult.getOrNull()
                    if (downloadUrl != null) {
                        images = listOf(downloadUrl)
                        Log.d(TAG, "Image uploaded successfully: $downloadUrl")
                    } else {
                        Log.e(TAG, "Failed to upload image for product ${product.id}: ${uploadResult.exceptionOrNull()?.message}")
                        return Result.retry()
                    }
                }

                val productToUpload = product.copy(images = images)

                // Subir a Firestore
                Log.d(TAG, "Saving product to Firestore: ${productToUpload.id}")
                remoteDataSource.updateProduct(productToUpload.toEntity())

                // Marcar como sincronizado en local
                localDataSource.markAsSynced(product.id)
                Log.d(TAG, "Product successfully synced and marked in Room: ${product.id}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Exception during product synchronization", e)
            Result.retry()
        }
    }
}
