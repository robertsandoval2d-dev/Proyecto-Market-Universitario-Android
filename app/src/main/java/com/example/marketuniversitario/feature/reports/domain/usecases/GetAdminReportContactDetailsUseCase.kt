package com.example.marketuniversitario.feature.reports.domain.usecases

import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import com.example.marketuniversitario.feature.reports.domain.models.AdminReportContactDetails
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetAdminReportContactDetailsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(report: Report): AdminReportContactDetails {
        val order = if (report.orderId.isNotBlank()) {
            orderRepository.getOrderRemote(report.orderId).getOrNull()
        } else null

        val buyerId = report.studentId.ifBlank { order?.buyerId.orEmpty() }
        val buyer = if (buyerId.isNotBlank()) {
            userRepository.getUserRemote(buyerId).getOrNull()
        } else null

        val sellerId = order?.sellerId.orEmpty()
        val seller = if (sellerId.isNotBlank()) {
            userRepository.getUserRemote(sellerId).getOrNull()
        } else null

        return AdminReportContactDetails(
            buyer = buyer,
            seller = seller,
            order = order
        )
    }
}
