package com.example.marketuniversitario.feature.orders.domain.models

data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)