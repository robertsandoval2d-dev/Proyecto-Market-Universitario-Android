package com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog

sealed interface OrderRequestStatus {
    data object Idle : OrderRequestStatus
    data object Loading : OrderRequestStatus
    data object Success : OrderRequestStatus
    data class Error(val message: String) : OrderRequestStatus
}