package com.example.marketuniversitario.feature.orders.domain.models

enum class OrderStatus(val displayName: String) {
    PENDING("Pendiente"),
    PREPARING("En preparación"),
    READY_FOR_PICKUP("Listo para entrega"),
    COMPLETED("Completado"),
    REJECTED("Rechazado")
}