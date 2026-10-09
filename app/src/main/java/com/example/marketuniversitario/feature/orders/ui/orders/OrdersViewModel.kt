package com.example.marketuniversitario.feature.orders.ui.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.orders.domain.models.OrderStatus
import com.example.marketuniversitario.feature.orders.domain.usecases.GetMyPurchasesUseCase
import com.example.marketuniversitario.feature.orders.domain.usecases.GetMySalesUseCase
import com.example.marketuniversitario.feature.orders.domain.usecases.UpdateOrderStatusUseCase
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getMyPurchasesUseCase: GetMyPurchasesUseCase,
    private val getMySalesUseCase: GetMySalesUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersState())
    val state = _state.asStateFlow()

    init {
        loadUserAndOrders()
    }
    private fun loadUserAndOrders() {
        val uid = authRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. Verificamos si tiene negocio
            userRepository.getUser(uid).onSuccess { user ->
                val hasBusiness = user?.hasBusiness == true
                val userName = user?.name ?: ""
                _state.update { 
                    it.copy(
                        hasBusiness = hasBusiness,
                        currentUserId = uid,
                        currentUserName = userName
                    ) 
                }

                // 2. Cargamos las Ventas
                if (hasBusiness) {
                    launch {
                        getMySalesUseCase()
                            .catch { error ->
                                _state.update { it.copy(errorMessage = "Error al cargar ventas: ${error.message}", isLoading = false) }
                            }
                            .collect { sales ->
                                _state.update { it.copy(sales = sales, isLoading = false) }
                            }
                    }
                }
            }

            // 3. Cargamos las Compras
            launch {
                getMyPurchasesUseCase()
                    .catch { error ->
                        Log.e("OrdersDebug", "Error en Firestore: ${error.message}", error)
                        _state.update { it.copy(errorMessage = "Error al cargar compras: ${error.message}", isLoading = false) }
                    }
                    .collect { purchases ->
                        Log.d("OrdersDebug", "Compras recibidas: ${purchases.size}")
                        _state.update { it.copy(purchases = purchases, isLoading = false) }
                    }
            }
        }
    }

    fun onEvent(event: OrdersEvent) {
        when (event) {
            is OrdersEvent.Refresh -> loadUserAndOrders()
            is OrdersEvent.TabChanged -> {
                _state.update { it.copy(selectedTab = event.tab) }
            }
            is OrdersEvent.AcceptOrder -> updateOrderStatus(event.orderId, OrderStatus.PREPARING)
            is OrdersEvent.RejectOrder -> updateOrderStatus(event.orderId, OrderStatus.REJECTED)
            is OrdersEvent.CompleteOrder -> updateOrderStatus(event.orderId, OrderStatus.COMPLETED)
        }
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            updateOrderStatusUseCase(orderId, newStatus).onFailure { error ->
                Log.e("OrdersDebug", "Error al actualizar estado del pedido", error)
                _state.update { it.copy(errorMessage = "No se pudo actualizar el pedido: ${error.message}") }
            }
        }
    }
}
