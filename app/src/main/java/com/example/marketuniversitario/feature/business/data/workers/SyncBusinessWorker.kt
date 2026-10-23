package com.example.marketuniversitario.feature.business.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.business.data.datasources.BusinessLocalDataSource
import com.example.marketuniversitario.feature.business.data.datasources.BusinessRemoteDataSource
import com.example.marketuniversitario.feature.business.data.datasources.ProductLocalDataSource
import com.example.marketuniversitario.feature.business.data.datasources.ProductRemoteDataSource
import com.example.marketuniversitario.feature.business.data.models.toDomain
import com.example.marketuniversitario.feature.business.data.models.toEntity
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessStorageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncBusinessWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: BusinessLocalDataSource,
    private val remoteDataSource: BusinessRemoteDataSource,
    private val storageRepository: BusinessStorageRepository,
    private val productLocalDataSource: ProductLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncBusinessWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedEntities = localDataSource.getUnsyncedBusinesses()
            Log.d(TAG, "Found ${unsyncedEntities.size} unsynced businesses to sync.")
            if (unsyncedEntities.isEmpty()) {
                return Result.success()
            }

            for (entity in unsyncedEntities) {
                val business = entity.toDomain()
                var bannerUrl = business.bannerUrl

                Log.d(TAG, "Syncing business: ${business.name} (ID: ${business.id})")

                // Si la imagen es local, subirla a Storage primero
                if (!bannerUrl.isNullOrBlank() && !bannerUrl.startsWith("http")) {
                    Log.d(TAG, "Uploading local banner image for business: $bannerUrl")
                    val uploadResult = storageRepository.uploadBusinessBanner(business.id, bannerUrl)
                    val downloadUrl = uploadResult.getOrNull()
                    if (downloadUrl != null) {
                        bannerUrl = downloadUrl
                        Log.d(TAG, "Banner image uploaded successfully: $downloadUrl")
                        // Actualizar de inmediato el URL permanente del banner en SQLite local
                        localDataSource.updateBusinessBannerUrl(business.id, downloadUrl)
                    } else {
                        Log.e(TAG, "Failed to upload banner image for business ${business.id}: ${uploadResult.exceptionOrNull()?.message}")
                        return Result.retry()
                    }
                }

                val businessToUpload = business.copy(bannerUrl = bannerUrl)

                // Subir a Firestore
                Log.d(TAG, "Saving business to Firestore: ${businessToUpload.id}")
                remoteDataSource.updateBusiness(businessToUpload.toEntity())

                // Actualizar el nombre del negocio en todos sus productos (Remoto y Local SQLite) con reintento si falla
                try {
                    productRemoteDataSource.updateBusinessNameInProducts(business.id, business.name)
                    productLocalDataSource.updateBusinessNameInProducts(business.id, business.name)
                    Log.d(TAG, "Business name updated in all associated products for: ${business.id}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to update business name in products during business sync", e)
                    return Result.retry()
                }

                // Marcar como sincronizado en local
                localDataSource.markAsSynced(business.id)
                Log.d(TAG, "Business successfully synced and marked in Room: ${business.id}")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Exception during business synchronization", e)
            Result.retry()
        }
    }
}
