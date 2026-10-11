package com.example.marketuniversitario.feature.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedProductsUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetUserSummaryUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
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
//    private val searchProducts: SearchProductsUseCase,
//    private val getFeed: GetFeedUseCase
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
            is HomeEvent.RequestProductSelected -> _state.update { it.copy(requestProductId = event.productId) }

            is HomeEvent.QueryChanged -> _state.update { it.copy(query = event.query) }
            is HomeEvent.CategorySelected -> _state.update { it.copy(selectedCategory = event.categoryId) }
            is HomeEvent.TagSelected -> _state.update { it.copy(selectedTagId = event.tagId) }
//            is HomeEvent.Search -> load()

            is HomeEvent.ShowSearchOptions -> _state.update { it.copy(showSearchOptions = true) }
            is HomeEvent.DismissSearchOptions -> _state.update { it.copy(showSearchOptions = false) }
            is HomeEvent.SelectImage -> _state.update { it.copy(selectedImage = event.uri) }

            is HomeEvent.ShowNotification -> _state.update { it.copy(showNotification = true) }
            is HomeEvent.DismissNotification -> _state.update { it.copy(showNotification = false) }
        }
    }

//    private var loadJob: Job? = null
//
//    private fun load() {
//        loadJob?.cancel()
//        loadJob = viewModelScope.launch {
//            _state.update { it.copy(status = HomeStatus.Loading) }
//            val state = _state.value
//            runCatching {
//                searchProducts(state.query!!)
//            }
//                .onSuccess { list -> _state.update { it.copy(products = list, status = HomeStatus.Success) } }
//                .onFailure { e ->
//                    if (e is CancellationException) throw e
//                    _state.update { it.copy(status = HomeStatus.Error(e.message ?: "Error al cargar productos")) }
//                }
//        }
//    }

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



    private fun refresh() {
        loadUser()
        loadProducts()
    }

}

