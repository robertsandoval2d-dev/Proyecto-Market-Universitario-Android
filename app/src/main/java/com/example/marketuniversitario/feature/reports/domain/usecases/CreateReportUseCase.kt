package com.example.marketuniversitario.feature.reports.domain.usecases

import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
import javax.inject.Inject

class CreateReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(report: Report): Result<Unit> {
        return reportRepository.createReport(report)
    }
}
