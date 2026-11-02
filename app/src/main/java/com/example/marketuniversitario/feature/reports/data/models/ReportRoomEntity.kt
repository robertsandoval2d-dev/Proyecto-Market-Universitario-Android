package com.example.marketuniversitario.feature.reports.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.models.ReportStatus

@Entity(tableName = "reports")
data class ReportRoomEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val businessId: String,
    val studentId: String,
    val reasonCategory: String,
    val description: String,
    val status: String,
    val orderStatusAtReport: String,
    val adminResolutionNotes: String?,
    val createdAt: Long,
    val isSynced: Boolean = true
)

fun Report.toRoomEntity(isSynced: Boolean = true) = ReportRoomEntity(
    id = this.id,
    orderId = this.orderId,
    businessId = this.businessId,
    studentId = this.studentId,
    reasonCategory = this.reasonCategory,
    description = this.description,
    status = this.status.value,
    orderStatusAtReport = this.orderStatusAtReport,
    adminResolutionNotes = this.adminResolutionNotes,
    createdAt = this.createdAt,
    isSynced = isSynced
)

fun ReportRoomEntity.toDomain() = Report(
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
