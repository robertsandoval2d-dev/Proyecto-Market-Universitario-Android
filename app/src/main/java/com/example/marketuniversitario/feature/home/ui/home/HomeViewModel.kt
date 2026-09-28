package com.example.marketuniversitario.feature.home.ui.home

import androidx.lifecycle.ViewModel
import com.example.marketuniversitario.feature.home.domain.models.mockProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadFeed -> loadMockData()
            is HomeEvent.Refresh -> refresh()

            is HomeEvent.QueryChanged -> _state.update { it.copy(query = event.query) }
            is HomeEvent.Search -> search()
            is HomeEvent.ShowSearchOptions -> _state.update { it.copy(showSearchOptions = true) }
            is HomeEvent.DismissSearchOptions -> _state.update { it.copy(showSearchOptions = false) }
            is HomeEvent.SelectImage -> _state.update { it.copy(selectedImage = event.uri) }

            is HomeEvent.ShowNotification -> _state.update { it.copy(showNotification = true) }
            is HomeEvent.DismissNotification -> _state.update { it.copy(showNotification = false) }
        }
    }

    private fun loadMockData() {
        val mockProducts = List(6) { i ->
            mockProduct.copy(id = "mock_$i", name = "Producto ${i + 1}")
        }
        _state.update {
            it.copy(products = mockProducts, status = HomeStatus.Success)
        }
    }

    private fun refresh() {
    }

    private fun search() {
    }





}

