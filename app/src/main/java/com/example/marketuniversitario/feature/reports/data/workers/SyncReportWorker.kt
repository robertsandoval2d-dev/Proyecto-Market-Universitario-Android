package com.example.marketuniversitario.feature.reports.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketuniversitario.feature.reports.data.datasources.ReportLocalDataSource
import com.example.marketuniversitario.feature.reports.data.datasources.ReportRemoteDataSource
import com.example.marketuniversitario.feature.reports.data.models.toDomain
import com.example.marketuniversitario.feature.reports.data.models.toEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncReportWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDataSource: ReportLocalDataSource,
    private val remoteDataSource: ReportRemoteDataSource
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncReportWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            val unsyncedReports = localDataSource.getUnsyncedReports()
            if (unsyncedReports.isEmpty()) return Result.success()

            for (roomEntity in unsyncedReports) {
                Log.d(TAG, "Syncing report: ${roomEntity.id}")
                val report = roomEntity.toDomain()
                remoteDataSource.createReport(report.toEntity())
                localDataSource.markAsSynced(roomEntity.id)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync reports", e)
            Result.retry()
        }
    }
}
