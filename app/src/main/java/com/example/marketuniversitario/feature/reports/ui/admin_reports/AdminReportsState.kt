package com.example.marketuniversitario.feature.reports.ui.admin_reports

import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.models.ReportStatus
import com.example.marketuniversitario.feature.user.domain.model.User

enum class ReportFilterTab(val displayName: String) {
    PENDING("Pendientes"),
    RESOLVED("Resueltos")
}

data class ContactDetails(
    val buyer: User? = null,
    val seller: User? = null,
    val order: Order? = null
)

data class AdminReportsState(
    val selectedFilter: ReportFilterTab = ReportFilterTab.PENDING,
    val reports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val selectedReportContacts: ContactDetails = ContactDetails(),
    val adminNotes: String = "",
    val isLoading: Boolean = false,
    val isResolving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val filteredReports: List<Report>
        get() = when (selectedFilter) {
            ReportFilterTab.PENDING -> reports.filter { it.status == ReportStatus.PENDING }
            ReportFilterTab.RESOLVED -> reports.filter { it.status != ReportStatus.PENDING }
        }
}

sealed interface AdminReportsEvent {
    data class FilterChanged(val filter: ReportFilterTab) : AdminReportsEvent
    data class SelectReport(val report: Report?) : AdminReportsEvent
    data class AdminNotesChanged(val notes: String) : AdminReportsEvent
    object RejectReport : AdminReportsEvent
    object ResolveAndCancelOrder : AdminReportsEvent
    object ClearMessages : AdminReportsEvent
}
