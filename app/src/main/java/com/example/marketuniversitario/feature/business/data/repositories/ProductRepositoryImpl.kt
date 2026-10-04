package com.example.marketuniversitario.feature.business.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.marketuniversitario.feature.business.data.datasources.ProductLocalDataSource
import com.example.marketuniversitario.feature.business.data.datasources.ProductRemoteDataSource
import com.example.marketuniversitario.feature.business.data.models.toDomain
import com.example.marketuniversitario.feature.business.data.models.toRoomEntity
import com.example.marketuniversitario.feature.business.data.workers.SyncProductWorker
import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class ProductRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localDataSource: ProductLocalDataSource,
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

    override suspend fun createProduct(product: Product): Result<Product> {
        return try {
            // 1. Guardar localmente primero (Offline-First) con isSynced = false
            val roomEntity = product.toRoomEntity(isSynced = false)
            localDataSource.saveProduct(roomEntity)

            // 2. Programar WorkManager único para sincronizar en segundo plano cuando haya conexión
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncProductWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_products_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            // 3. Retornar éxito inmediatamente a la UI desde la base de datos local
            Result.success(roomEntity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProduct(productId: String): Result<Product?> {
        return try {
            // 1. Intentar actualizar desde remoto con un timeout rápido (3 segundos) si hay red
            try {
                withTimeoutOrNull(3000.milliseconds) {
                    val remoteEntity = remoteDataSource.getProduct(productId)
                    if (remoteEntity != null) {
                        localDataSource.saveProduct(remoteEntity.toDomain().toRoomEntity(isSynced = true))
                    }
                }
            } catch (_: Exception) {
                // Ignorar error de red / timeout
            }

            // 2. Devolver desde la base de datos local (Room)
            val localEntity = localDataSource.getProductById(productId)
            Result.success(localEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByBusiness(businessId: String): Result<List<Product>> {
        return try {
            // 1. Sincronización Remote -> Local con timeout rápido (3 segundos).
            // Si hay internet, descarga lo remoto y actualiza Room.
            // Si no hay internet o es lento, expira al instante sin congelar la app.
            try {
                withTimeoutOrNull(3000.milliseconds) {
                    val remoteEntities = remoteDataSource.getProductsByBusiness(businessId)
                    for (remoteEntity in remoteEntities) {
                        localDataSource.saveProduct(remoteEntity.toDomain().toRoomEntity(isSynced = true))
                    }
                }
            } catch (_: Exception) {
                // Ignorar fallos de red o timeout y continuar con la caché local
            }

            // 2. Fuente de verdad local (Room): siempre devuelve los datos al instante
            val localEntities = localDataSource.getProductsByBusiness(businessId)
            Result.success(localEntities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val remoteEntities = remoteDataSource.getProducts()
            Result.success(remoteEntities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val roomEntity = product.toRoomEntity(isSynced = false)
            localDataSource.saveProduct(roomEntity)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncProductWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_products_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBusinessNameInProducts(businessId: String, newBusinessName: String): Result<Unit> {
        return try {
            remoteDataSource.updateBusinessNameInProducts(businessId, newBusinessName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            localDataSource.deleteProduct(productId)
            remoteDataSource.deleteProduct(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
