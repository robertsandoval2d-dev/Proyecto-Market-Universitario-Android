package com.example.marketuniversitario.feature.home.ui.home

import android.net.Uri
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.home.domain.models.OrderRequest
import com.example.marketuniversitario.feature.home.domain.models.UserSummary
import com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog.OrderRequestStatus
import com.example.marketuniversitario.feature.orders.domain.models.Order

sealed interface HomeStatus {
    object Idle : HomeStatus
    object Loading : HomeStatus
    object Success : HomeStatus
    data class Error(val message: String) : HomeStatus
}
data class HomeState(
    //User Summary
    val userSummary: UserSummary? = null,

    //Notification
    val showNotification: Boolean = false,

    //Búsqueda
    val query: String = "",
//    val selectedCategory: String? = null,

    //Other search options
    val showSearchOptions: Boolean = false,
    val selectedImage: Uri = Uri.EMPTY,

    //Lista productos
    val products: List<FeedProduct> = emptyList(),
    val selectedProduct: FeedProduct? = null,
    val requestProduct: FeedProduct? = null,

    val requestOrder: Order? = null,

    val status: HomeStatus = HomeStatus.Idle,
    val orderRequestStatus: OrderRequestStatus = OrderRequestStatus.Idle
)

sealed interface HomeEvent {

    //Eventos para userInfo
    data object LoadUser : HomeEvent

    //Eventos para productos
    data object LoadProducts : HomeEvent
    object LoadFeed : HomeEvent
    object Refresh : HomeEvent
    data class SelectProductQuickView(val product: FeedProduct?) : HomeEvent
    data class RequestProductView(val product: FeedProduct?) : HomeEvent
    data class RequestOrder(val order: OrderRequest) : HomeEvent

    object DismissOrderRequestStatus : HomeEvent


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