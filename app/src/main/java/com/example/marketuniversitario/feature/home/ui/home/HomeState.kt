package com.example.marketuniversitario.feature.home.ui.home

import android.net.Uri
import com.example.marketuniversitario.feature.business.domain.models.Product

sealed interface HomeStatus {
    object Idle : HomeStatus
    object Loading : HomeStatus
    object Success : HomeStatus
    data class Error(val message: String) : HomeStatus
}
data class HomeState(
    val query: String = "",
    val selectedCategory: String? = null,
    val products: List<Product> = emptyList(),
    val showSearchOptions: Boolean = false,
    val showNotification: Boolean = false,
    val selectedImage: Uri = Uri.EMPTY,
    val status: HomeStatus = HomeStatus.Idle
)

sealed interface HomeEvent {
    object LoadFeed : HomeEvent
    object Refresh : HomeEvent

    //Eventos para productos

    //Eventos barra búsqueda
    data class QueryChanged(val query: String) : HomeEvent
    object Search : HomeEvent

    //Eventos buscar con imagen
    object ShowSearchOptions : HomeEvent
    object DismissSearchOptions : HomeEvent
    data class SelectImage(val uri: Uri) : HomeEvent

    //Eventos para notificación
    object ShowNotification : HomeEvent
    object DismissNotification : HomeEvent


//    data class CategorySelected(val categoryId: String?) : HomeEvent
}