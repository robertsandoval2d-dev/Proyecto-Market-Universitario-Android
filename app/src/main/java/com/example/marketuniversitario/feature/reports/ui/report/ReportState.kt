package com.example.marketuniversitario.feature.reports.ui.report

import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.models.ReportConstants

sealed interface ReportStatus {
    object Idle : ReportStatus
    object Loading : ReportStatus
    object Success : ReportStatus
    data class Error(val message: String) : ReportStatus
}

data class ReportState(
    val orderId: String = "",
    val businessId: String = "",
    val studentId: String = "",
    val orderStatusAtReport: String = "",
    val reasonCategory: String = ReportConstants.reportReasons.first(),
    val description: String = "",
    val status: ReportStatus = ReportStatus.Idle,
    val existingReport: Report? = null
)

sealed interface ReportEvent {
    data class InitData(
        val orderId: String,
        val businessId: String,
        val studentId: String,
        val orderStatusAtReport: String
    ) : ReportEvent
    data class ReasonChanged(val reason: String) : ReportEvent
    data class DescriptionChanged(val description: String) : ReportEvent
    object SubmitReport : ReportEvent
    object ResetStatus : ReportEvent
    object ResetForm : ReportEvent
}
