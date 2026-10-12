package com.example.marketuniversitario.feature.orders.domain.usecases

import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.ProductRequest
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.repositories.OrderRepository
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository

import javax.inject.Inject

class CreateOrderRequestUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val businessRepository: BusinessRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(request: ProductRequest): Result<Order> {
        val uid = authRepository.getCurrentUserId()?: throw Exception("No se encontró el usuario actual")
        val clientInfo = userRepository.getUser(uid).fold(
            onSuccess = { client ->
                client ?: throw Exception("No se encontró la información del cliente")
            },
            onFailure = {
                throw Exception("No se pudo obtener la información del cliente")
            }
        )
        val businessInfo = businessRepository.getBusiness(request.businessId).fold(
            onSuccess = { business ->
                business ?: throw Exception("No se encontró la información del negocio")
            },
            onFailure = {
                throw Exception("No se pudo obtener la información del negocio")
            }
        )

        if (businessInfo.ownerId == uid || clientInfo.businessId == businessInfo.id) {
            throw Exception("No puedes crear una orden para tu propio negocio")
        }

        val order = Order(
            buyerId = clientInfo.id,
            buyerName = clientInfo.name,
            buyerPhone = clientInfo.phone,

            sellerId = businessInfo.ownerId,
            businessId = businessInfo.id,
            businessName = businessInfo.name,

            productId = request.id,
            productName = request.name,
            productImage = request.imageUrl,
            productPrice = request.price,

            quantity = request.quantity,
            totalPrice = request.quantity * request.price,
            note = request.note,

            status = OrderStatus.PENDING
        )

        return orderRepository.createOrder(order)
    }

}