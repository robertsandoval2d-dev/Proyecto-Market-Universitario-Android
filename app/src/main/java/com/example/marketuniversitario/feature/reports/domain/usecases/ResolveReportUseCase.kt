package com.example.marketuniversitario.feature.reports.domain.usecases

import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
import javax.inject.Inject

class ResolveReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        reportId: String,
        orderId: String,
        businessId: String,
        resolutionStatus: String,
        adminNotes: String,
        cancelOrder: Boolean
    ): Result<Unit> {
        val order = if (orderId.isNotBlank()) orderRepository.getOrderById(orderId) else null
        val isOrderCompleted = order?.status == OrderStatus.COMPLETED
        val targetBusinessId = businessId.ifBlank { order?.businessId.orEmpty() }

        return reportRepository.resolveReportWithBatch(
            reportId = reportId,
            orderId = orderId,
            businessId = targetBusinessId,
            resolutionStatus = resolutionStatus,
            adminNotes = adminNotes,
            cancelOrder = cancelOrder,
            isOrderCompleted = isOrderCompleted
        )
    }
}
