package com.example.marketuniversitario.feature.reports.data.models

import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.models.ReportStatus
import com.google.firebase.firestore.DocumentId

data class ReportEntity(
    @DocumentId val id: String = "",
    val orderId: String = "",
    val businessId: String = "",
    val studentId: String = "", // Quien reporta
    val reasonCategory: String = "",
    val description: String = "",
    val status: String = "PENDING", // PENDING, RESOLVED, REJECTED
    // Ej: "IN_PROGRESS", "COMPLETED", "CANCELLED"
    val orderStatusAtReport: String = "",
    // Auditoría: Para que el admin guarde por qué tomó esa decisión
    val adminResolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun Report.toEntity() = ReportEntity(
    id = this.id,
    orderId = this.orderId,
    businessId = this.businessId,
    studentId = this.studentId,
    reasonCategory = this.reasonCategory,
    description = this.description,
    status = this.status.value,
    orderStatusAtReport = this.orderStatusAtReport,
    adminResolutionNotes = this.adminResolutionNotes,
    createdAt = this.createdAt
)

fun ReportEntity.toDomain() = Report(
    id = this.id,
    orderId = this.orderId,
    businessId = this.businessId,
    studentId = this.studentId,
    reasonCategory = this.reasonCategory,
    description = this.description,
    status = ReportStatus.fromString(this.status),
    orderStatusAtReport = this.orderStatusAtReport,
    adminResolutionNotes = this.adminResolutionNotes,
    createdAt = this.createdAt
)
