package com.example.marketuniversitario.feature.reports.data.repositories

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.marketuniversitario.feature.reports.data.datasources.ReportLocalDataSource
import com.example.marketuniversitario.feature.reports.data.datasources.ReportRemoteDataSource
import com.example.marketuniversitario.feature.reports.data.models.toDomain
import com.example.marketuniversitario.feature.reports.data.models.toRoomEntity
import com.example.marketuniversitario.feature.reports.data.workers.SyncReportWorker
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val remoteDataSource: ReportRemoteDataSource,
    private val localDataSource: ReportLocalDataSource
) : ReportRepository {

    override suspend fun createReport(report: Report): Result<Unit> {
        return try {
            val reportId = if (report.id.isBlank()) UUID.randomUUID().toString() else report.id
            val reportToSave = report.copy(id = reportId)

            // 1. Guardar localmente primero (Offline-first)
            val roomEntity = reportToSave.toRoomEntity(isSynced = false)
            localDataSource.insertReport(roomEntity)

            // 2. Programar WorkManager para sincronizar en segundo plano cuando haya conexión
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncReportWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "sync_reports_work",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resolveReport(
        reportId: String,
        resolutionStatus: String,
        adminNotes: String
    ): Result<Unit> {
        return try {
            remoteDataSource.resolveReport(reportId, resolutionStatus, adminNotes)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resolveReportWithBatch(
        reportId: String,
        orderId: String,
        businessId: String,
        resolutionStatus: String,
        adminNotes: String,
        cancelOrder: Boolean,
        isOrderCompleted: Boolean
    ): Result<Unit> {
        return try {
            remoteDataSource.resolveReportWithBatch(
                reportId = reportId,
                orderId = orderId,
                businessId = businessId,
                resolutionStatus = resolutionStatus,
                adminNotes = adminNotes,
                cancelOrder = cancelOrder,
                isOrderCompleted = isOrderCompleted
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReportByOrderId(orderId: String): Result<Report?> {
        return try {
            // 1. Intentar local
            val localEntity = localDataSource.getReportByOrderId(orderId)
            if (localEntity != null) {
                return Result.success(localEntity.toDomain())
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPendingReports(): Flow<List<Report>> {
        // Admin: Solo remoto en tiempo real (sin persistencia local)
        return remoteDataSource.getPendingReports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllReports(): Flow<List<Report>> {
        return remoteDataSource.getAllReports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getReportsByStudent(studentId: String): Flow<List<Report>> {
        // Sincronizar remotamente a SQLite en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                remoteDataSource.getReportsByStudent(studentId).collect { entities ->
                    val roomEntities = entities.map { it.toDomain().toRoomEntity(isSynced = true) }
                    localDataSource.insertReports(roomEntities)
                }
            } catch (_: Exception) {}
        }
        // Consultar siempre desde SQLite local (Offline-first para el estudiante)
        return localDataSource.getReportsByStudent(studentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
