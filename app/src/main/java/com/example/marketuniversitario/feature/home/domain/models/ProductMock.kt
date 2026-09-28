package com.example.marketuniversitario.feature.home.domain.models

import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.Product

val mockProduct = Product(
    id = "1",
    businessId = "business-1",
    businessName = "Tienda Universitaria",
    name = "Audífonos Bluetooth",
    description = "Audífonos inalámbricos",
    price = 89.90,
    type = ItemType.PRODUCT.name,
    category = "Tecnología",
    stock = 10,
    tags = listOf("audio", "bluetooth"),
    views = 100,
    salesCount = 20,
    favoritesCount = 15,
    rating = 4.5
)