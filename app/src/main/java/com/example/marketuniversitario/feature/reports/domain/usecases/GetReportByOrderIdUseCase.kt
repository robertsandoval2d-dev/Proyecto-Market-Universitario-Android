package com.example.marketuniversitario.feature.reports.domain.usecases

import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
import javax.inject.Inject

class GetReportByOrderIdUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(orderId: String): Result<Report?> {
        return reportRepository.getReportByOrderId(orderId)
    }
}
