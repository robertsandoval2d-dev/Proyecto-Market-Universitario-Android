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
    val hasBusiness: Boolean = false,
    val purchases: List<Order> = emptyList(),
    val sales: List<Order> = emptyList(),
    val currentUserId: String = "",
    val currentUserName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentOrders: List<Order>
        get() = if (selectedTab == OrderTab.PURCHASES) purchases else sales
}

// Eventos de la pantalla
sealed interface OrdersEvent {
    object Refresh : OrdersEvent
    data class TabChanged(val tab: OrderTab) : OrdersEvent
    data class AcceptOrder(val orderId: String) : OrdersEvent
    data class RejectOrder(val orderId: String) : OrdersEvent
    data class CompleteOrder(val orderId: String) : OrdersEvent
}
