package com.example.marketuniversitario.feature.reports.domain.usecases

import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReportsByStudentUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    operator fun invoke(studentId: String): Flow<List<Report>> {
        return reportRepository.getReportsByStudent(studentId)
    }
}
