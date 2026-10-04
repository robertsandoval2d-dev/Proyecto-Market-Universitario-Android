package com.example.marketuniversitario.feature.orders.ui.orders

import androidx.lifecycle.ViewModel
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor() : ViewModel() {

    private val allMockOrders = mutableListOf(
        Order("1", "Calculadora Científica Casio", "", 45.0, 1, OrderStatus.PENDING, "04 Oct, 10:30 AM", isSale = false),
        Order("2", "Menú Almuerzo - FISI", "", 12.0, 2, OrderStatus.PREPARING, "04 Oct, 11:15 AM", isSale = false),
        Order("4", "Brownie de Chocolate", "", 4.5, 3, OrderStatus.PENDING, "04 Oct, 02:20 PM", isSale = true)
    )

    private val _state = MutableStateFlow(OrdersState())
    val state = _state.asStateFlow()

    init {
        filterOrders()
    }

    fun onEvent(event: OrdersEvent) {
        when (event) {
            is OrdersEvent.TabChanged -> {
                _state.update { it.copy(selectedTab = event.tab) }
                filterOrders()
            }
            is OrdersEvent.AcceptOrder -> updateOrderStatus(event.orderId, OrderStatus.PREPARING)
            is OrdersEvent.RejectOrder -> updateOrderStatus(event.orderId, OrderStatus.REJECTED)
        }
    }

    private fun filterOrders() {
        val currentTab = _state.value.selectedTab
        val filteredList = allMockOrders.filter { if (currentTab == OrderTab.PURCHASES) !it.isSale else it.isSale }
        _state.update { it.copy(orders = filteredList) }
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val index = allMockOrders.indexOfFirst { it.id == orderId }
        if (index != -1) {
            allMockOrders[index] = allMockOrders[index].copy(status = newStatus)
            filterOrders()
        }
    }
}