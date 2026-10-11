package com.example.marketuniversitario.feature.orders.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.orders.data.datasources.OrderLocalDataSource
import com.example.marketuniversitario.feature.orders.data.datasources.OrderRemoteDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncOrderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: OrderLocalDataSource,
    private val remoteDataSource: OrderRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncOrderWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedOrders = localDataSource.getUnsyncedOrders()
            if (unsyncedOrders.isEmpty()) return Result.success()

            for (order in unsyncedOrders) {
                Log.d(TAG, "Syncing order status update: ${order.id} -> ${order.status}")
                remoteDataSource.updateOrderStatus(order.id, order.status)
                localDataSource.markAsSynced(order.id)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync orders", e)
            Result.retry()
        }
    }
}
