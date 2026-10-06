package com.example.marketuniversitario.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.home.domain.models.OrderRequest
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedProductsUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetUserSummaryUseCase
import com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog.OrderRequestStatus
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.usecases.CreateOrderRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserSummaryUseCase: GetUserSummaryUseCase,
    private val getFeedProductsUseCase: GetFeedProductsUseCase,
    private val createOrderRequestUseCase: CreateOrderRequestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadUser()
        loadProducts()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadFeed, is HomeEvent.LoadProducts -> loadProducts()
            is HomeEvent.Refresh -> refresh()

            is HomeEvent.LoadUser -> loadUser()
            is HomeEvent.SelectProductQuickView -> _state.update { it.copy(selectedProduct = event.product) }
            is HomeEvent.RequestProductView -> _state.update { it.copy(requestProduct = event.product) }
            is HomeEvent.RequestOrder -> sendRequest(event.order)
            is HomeEvent.DismissOrderRequestStatus -> _state.update { it.copy(orderRequestStatus = OrderRequestStatus.Idle) }


            is HomeEvent.QueryChanged -> _state.update { it.copy(query = event.query) }
            is HomeEvent.Search -> search()
            is HomeEvent.ShowSearchOptions -> _state.update { it.copy(showSearchOptions = true) }
            is HomeEvent.DismissSearchOptions -> _state.update { it.copy(showSearchOptions = false) }
            is HomeEvent.SelectImage -> _state.update { it.copy(selectedImage = event.uri) }

            is HomeEvent.ShowNotification -> _state.update { it.copy(showNotification = true) }
            is HomeEvent.DismissNotification -> _state.update { it.copy(showNotification = false) }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val userInfo = getUserSummaryUseCase()

                _state.update {
                    it.copy(
                        userSummary = userInfo
                    )
                }
            } catch (_: Exception) {
                // Si el usuario no está aún disponible o falla, mantenemos userSummary como null (la UI usa "Usuario" por defecto)
            }
        }

    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(status = HomeStatus.Loading) }
            getFeedProductsUseCase()
                .onSuccess { productList ->
                    _state.update {
                        it.copy(
                            products = productList,
                            status = HomeStatus.Success
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            status = HomeStatus.Error(error.message ?: "Error al cargar productos")
                        )
                    }
                }
        }
    }

    private fun sendRequest(request: OrderRequest){

        viewModelScope.launch {
            _state.update { it.copy(orderRequestStatus = OrderRequestStatus.Loading) }
            createOrderRequestUseCase(request)
                .onSuccess {
                    _state.update {
                        it.copy(
                            requestProduct = null,
                            orderRequestStatus = OrderRequestStatus.Success
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            orderRequestStatus = OrderRequestStatus.Error(error.message ?: "Error al enviar la solicitud")
                        )
                    }
                }
        }
    }

    private fun refresh() {
        loadUser()
        loadProducts()
    }

    private fun search() {
    }
}

