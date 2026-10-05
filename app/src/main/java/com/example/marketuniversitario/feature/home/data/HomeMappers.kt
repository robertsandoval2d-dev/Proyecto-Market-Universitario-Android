package com.example.marketuniversitario.feature.home.data

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.home.domain.models.UserSummary
import com.example.marketuniversitario.feature.user.domain.model.User

fun User.toUserSummary(): UserSummary {
    val firstName = this.name
        .trim()
        .split("\\s+".toRegex())
        .firstOrNull()
        ?.lowercase()
        ?.replaceFirstChar { it.uppercase() }
        ?: ""

    return UserSummary(
        id = this.id,
        firstName = firstName,
        photoUrl = this.photoUrl
    )
}

fun Product.toFeedProduct(): FeedProduct {
    return FeedProduct(
        id = this.id,
        businessId = this.businessId,
        businessName = this.businessName,
        name = this.name,
        price = this.price,
        imageUrl = this.images.firstOrNull() ?: ""
    )
}