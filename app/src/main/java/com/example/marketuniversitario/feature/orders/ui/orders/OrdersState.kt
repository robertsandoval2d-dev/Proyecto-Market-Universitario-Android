package com.example.marketuniversitario.feature.orders.ui.orders

import com.example.marketuniversitario.feature.orders.domain.models.Order

// Pestañas de la UI
enum class OrderTab(val title: String) {
    PURCHASES("Mis Compras"),
    SALES("Mis Ventas")
}

// Estado de la pantalla
data class OrdersState(
    val selectedTab: OrderTab = OrderTab.PURCHASES,
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false
)

// Eventos de la pantalla
sealed interface OrdersEvent {
    data class TabChanged(val tab: OrderTab) : OrdersEvent
    data class AcceptOrder(val orderId: String) : OrdersEvent
    data class RejectOrder(val orderId: String) : OrdersEvent
}