package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.example.marketuniversitario.feature.home.domain.models.OrderRequest
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository

import javax.inject.Inject

class CreateOrderRequestUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: OrderRequest): Result<Order> {
        val uid = authRepository.getCurrentUserId()?: throw Exception("No se encontró el usuario actual")
        val clientInfo = userRepository.getUser(uid).fold(
            onSuccess = { client ->
                client ?: throw Exception("No se encontró la información del cliente")
            },
            onFailure = {
                throw Exception("No se pudo obtener la información del cliente")
            }
        )
        val businessInfo = businessRepository.getBusiness(request.product.businessId).fold(
            onSuccess = { business ->
                business ?: throw Exception("No se encontró la información del negocio")
            },
            onFailure = {
                throw Exception("No se pudo obtener la información del negocio")
            }
        )

        val order = Order(
            buyerId = clientInfo.id,
            buyerName = clientInfo.name,
            buyerPhone = clientInfo.phone,

            sellerId = businessInfo.ownerId,
            businessId = businessInfo.id,
            businessName = businessInfo.name,

            productId = request.product.id,
            productName = request.product.name,
            productImage = request.product.imageUrl,
            productPrice = request.product.price,

            quantity = request.quantity,
            totalPrice = request.quantity * request.product.price,
            note = request.note,

            status = OrderStatus.PENDING
        )

        return orderRepository.createOrder(order)
    }

}