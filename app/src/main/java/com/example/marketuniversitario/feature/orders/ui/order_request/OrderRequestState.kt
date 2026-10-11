package com.example.marketuniversitario.feature.orders.ui.order_request

import com.example.marketuniversitario.feature.orders.domain.models.ProductRequest

sealed interface OrderRequestStatus {
    data object Idle : OrderRequestStatus
    data object Loading : OrderRequestStatus
    data object Success : OrderRequestStatus
    data class Error(val message: String) : OrderRequestStatus
}

data class OrderRequestState(
    val quantity: Int = 1,
    val note: String = "",
    val productRequest: ProductRequest? = null,
    val status: OrderRequestStatus = OrderRequestStatus.Idle
)

sealed interface OrderRequestEvent {
    data class QuantityChanged(val quantity: Int) : OrderRequestEvent
    data class NoteChanged(val note: String) : OrderRequestEvent
    data class RequestOrder(val productRequest: ProductRequest) : OrderRequestEvent
    object Dismiss : OrderRequestEvent
}
