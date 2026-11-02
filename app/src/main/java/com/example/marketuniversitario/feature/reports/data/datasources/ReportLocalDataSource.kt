package com.example.marketuniversitario.feature.reports.data.datasources

import com.example.marketuniversitario.feature.reports.data.models.ReportRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportLocalDataSource @Inject constructor(
    private val reportDao: ReportDao
) {
    fun getReportsByStudent(studentId: String): Flow<List<ReportRoomEntity>> {
        return reportDao.getReportsByStudent(studentId)
    }

    suspend fun getReportByOrderId(orderId: String): ReportRoomEntity? {
        return reportDao.getReportByOrderId(orderId)
    }

    suspend fun getUnsyncedReports(): List<ReportRoomEntity> {
        return reportDao.getUnsyncedReports()
    }

    suspend fun markAsSynced(reportId: String) {
        reportDao.markAsSynced(reportId)
    }

    suspend fun insertReports(reports: List<ReportRoomEntity>) {
        reportDao.insertReports(reports)
    }

    suspend fun insertReport(report: ReportRoomEntity) {
        reportDao.insertReport(report)
    }
}
