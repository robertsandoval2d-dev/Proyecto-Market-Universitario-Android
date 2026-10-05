package com.example.marketuniversitario.feature.home.domain.models

data class OrderRequest (
    val product: FeedProduct,
    val quantity: Int = 1,
    val note: String = ""
)