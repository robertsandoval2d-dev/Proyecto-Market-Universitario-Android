package com.example.marketuniversitario.feature.reports.domain.repositories

import com.example.marketuniversitario.feature.reports.domain.models.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {

    suspend fun createReport(report: Report): Result<Unit>

    suspend fun resolveReport(reportId: String, resolutionStatus: String, adminNotes: String): Result<Unit>

    suspend fun resolveReportWithBatch(
        reportId: String,
        orderId: String,
        businessId: String,
        resolutionStatus: String,
        adminNotes: String,
        cancelOrder: Boolean,
        isOrderCompleted: Boolean
    ): Result<Unit>

    suspend fun getReportByOrderId(orderId: String): Result<Report?>

    fun getPendingReports(): Flow<List<Report>>

    fun getAllReports(): Flow<List<Report>>

    fun getReportsByStudent(studentId: String): Flow<List<Report>>
}
