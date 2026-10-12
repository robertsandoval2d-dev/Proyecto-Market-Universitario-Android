package com.example.marketuniversitario.feature.orders.ui.order_request

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.usecases.GetPublicProductUseCase
import com.example.marketuniversitario.feature.orders.domain.models.ProductRequest
import com.example.marketuniversitario.feature.orders.domain.usecases.CreateOrderRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.isNullOrBlank

@HiltViewModel
class OrderRequestViewModel @Inject constructor(
    private val createOrderRequestUseCase: CreateOrderRequestUseCase,
    private val getPublicProductUseCase: GetPublicProductUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(OrderRequestState())
    val state: StateFlow<OrderRequestState> = _state.asStateFlow()

    init {
        val productId = savedStateHandle.get<String>("productId")
        if (productId.isNullOrBlank()) {
            _state.update { it.copy(status = OrderRequestStatus.Error("Producto no válido")) }
        } else {
            loadOrderRequest(productId)
        }
    }

    fun onEvent(event: OrderRequestEvent) {
        when (event) {
            is OrderRequestEvent.RequestOrder -> sendRequest(event.productRequest)
            is OrderRequestEvent.QuantityChanged -> _state.update { it.copy(quantity = event.quantity) }
            is OrderRequestEvent.NoteChanged -> _state.update { it.copy(note = event.note) }
            is OrderRequestEvent.Dismiss -> _state.update {
                it.copy(
                    quantity = 1,
                    note = "",
                    status = OrderRequestStatus.Idle
                )
            }
        }
    }

    private fun loadOrderRequest(productId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy( status = OrderRequestStatus.Loading )
            }
            getPublicProductUseCase(productId)
                .onSuccess { product ->
                    if (product == null) {
                        _state.update {
                            it.copy(
                                status = OrderRequestStatus.Error("No se encontró el producto")
                            )
                        }
                        return@onSuccess
                    }
                    _state.update {
                        it.copy(
                            quantity = 1,
                            note = "",
                            productRequest = ProductRequest(
                                id = product.id,
                                businessId = product.businessId,
                                businessName = product.businessName,
                                name = product.name,
                                imageUrl = product.images.firstOrNull() ?: "",
                                price = product.price
                            ),
                            status = OrderRequestStatus.Idle
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            status = OrderRequestStatus.Error(error.message ?: "Error al cargar datos del producto")
                        )
                    }
                }
        }
    }

    private fun sendRequest(request: ProductRequest) {
        if (_state.value.status is OrderRequestStatus.Loading) return
        viewModelScope.launch {
            _state.update { it.copy(status = OrderRequestStatus.Loading) }
            createOrderRequestUseCase(request)
                .onSuccess {
                    _state.update {
                        it.copy(
                            quantity = 1,
                            note = "",
                            productRequest = null,
                            status = OrderRequestStatus.Success
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            status = OrderRequestStatus.Error(error.message ?: "Error al enviar la solicitud")
                        )
                    }
                }
        }
    }
}
