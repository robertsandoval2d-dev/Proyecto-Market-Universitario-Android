package com.example.marketuniversitario.feature.home.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.GetCurrentUserIdUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetPublicProductUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.IncrementProductViewUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedProductsUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetUserSummaryUseCase
import com.example.marketuniversitario.feature.recomendation.domain.models.InteractionType
import com.example.marketuniversitario.feature.recomendation.domain.usecases.TrackInteractionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserSummaryUseCase: GetUserSummaryUseCase,
    private val getFeedProductsUseCase: GetFeedProductsUseCase,
    private val getPublicProductUseCase: GetPublicProductUseCase,
    private val incrementProductViewUseCase: IncrementProductViewUseCase,
    private val trackInteractionUseCase: TrackInteractionUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
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
            is HomeEvent.SelectProductQuickView -> {
                _state.update { it.copy(selectedProduct = event.product) }
                if (event.product != null) {
                    viewModelScope.launch {
                        incrementProductViewUseCase(event.product.id)
                            .onSuccess {
                                Log.d("HomeViewModel", "Vista de producto incrementada en QuickView: ${event.product.id}")
                            }
                            .onFailure { error ->
                                Log.e("HomeViewModel", "Fallo al incrementar vista en QuickView: ${error.message}", error)
                            }

                        val uid = getCurrentUserIdUseCase()
                        if (!uid.isNullOrBlank()) {
                            val product = getPublicProductUseCase(event.product.id).getOrNull()
                            if (product != null) {
                                trackInteractionUseCase(uid, product, InteractionType.VIEW)
                                    .onSuccess {
                                        Log.d("HomeViewModel", "Interacción VIEW registrada en QuickView para usuario: $uid")
                                    }
                                    .onFailure { error ->
                                        Log.e("HomeViewModel", "Fallo al registrar interacción VIEW en QuickView: ${error.message}", error)
                                    }
                            }
                        }
                    }
                }
            }
            is HomeEvent.RequestProductSelected -> _state.update { it.copy(requestProductId = event.productId) }

            is HomeEvent.QueryChanged -> _state.update { it.copy(query = event.query) }
            is HomeEvent.CategorySelected -> _state.update { it.copy(selectedCategory = event.categoryId) }
            is HomeEvent.TagSelected -> _state.update { it.copy(selectedTagId = event.tagId) }

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

            }
        }

    }

    private var productsJob: Job? = null
    private fun loadProducts() {
        productsJob?.cancel()
        productsJob = viewModelScope.launch {
            _state.update { it.copy(status = HomeStatus.Loading) }
            getFeedProductsUseCase()
                .catch { e -> _state.update { it.copy(status = HomeStatus.Error(e.message ?: "Error al cargar la lista de productos")) } }
                .onEach { products ->
                    _state.update {
                        it.copy(products = products,
                            status = HomeStatus.Success)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun refresh() {
        loadUser()
        loadProducts()
    }
}
