package com.example.marketuniversitario.feature.orders.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.orders.data.datasources.OrderLocalDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.OrderRemoteDataSource
import com.example.marketuniversitario.feature.orders.data.models.toDomain
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.recomendation.domain.models.InteractionType
import com.example.marketuniversitario.feature.recomendation.domain.repositories.InteractionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncOrderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: OrderLocalDataSource,
    private val remoteDataSource: OrderRemoteDataSource,
    private val productRepository: ProductRepository,
    private val interactionRepository: InteractionRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncOrderWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedOrders = localDataSource.getUnsyncedOrders()
            if (unsyncedOrders.isEmpty()) return Result.success()

            for (roomEntity in unsyncedOrders) {
                val order = roomEntity.toDomain()
                Log.d(TAG, "Syncing order status update: ${order.id} -> ${order.status}")
                remoteDataSource.updateOrderStatus(order.id, order.status.name)

                // Si la orden está completada, sincronizamos los affinityScores del comprador en Firestore
                if (order.status == OrderStatus.COMPLETED && order.buyerId.isNotBlank() && order.productId.isNotBlank()) {
                    try {
                        val product = productRepository.getProduct(order.productId).getOrNull()
                        if (product != null) {
                            val tagsToUpdate = (listOf(product.category) + product.tags).filter { it.isNotBlank() }
                            if (tagsToUpdate.isNotEmpty()) {
                                interactionRepository.incrementAffinityScores(
                                    userId = order.buyerId,
                                    tags = tagsToUpdate,
                                    weight = InteractionType.PURCHASE.weight
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update buyer affinity scores for completed order ${order.id}", e)
                    }
                }

                localDataSource.markAsSynced(order.id)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync orders", e)
            if (runAttemptCount < 5) Result.retry() else Result.failure()
        }
    }
}
