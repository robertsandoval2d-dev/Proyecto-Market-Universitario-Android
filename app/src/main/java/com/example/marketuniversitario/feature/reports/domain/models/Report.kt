package com.example.marketuniversitario.feature.reports.domain.models

data class Report(
    val id: String = "",
    val orderId: String = "",
    val businessId: String = "",
    val studentId: String = "", // Quien reporta
    val reasonCategory: String = "",
    val description: String = "",
    val status: ReportStatus = ReportStatus.PENDING,
    val orderStatusAtReport: String = "",
    val adminResolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
