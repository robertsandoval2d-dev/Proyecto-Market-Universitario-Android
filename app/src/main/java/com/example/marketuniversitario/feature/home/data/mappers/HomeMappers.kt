package com.example.marketuniversitario.feature.home.data.mappers

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
        category = this.category,
        imageUrl = this.images.firstOrNull() ?: "",
        isAvailable = this.isAvailable,
        allTags = this.tags,
        searchKeywords = this.buildSearchKeywords(),
        createdAt = this.createdAt
    )
}

/**
 * Palabras extra para el buscador: descripción y nombre del negocio.
 * Se calcula al mapear; no se guarda en Firestore. El buscador normaliza
 * tildes y mayúsculas, aquí solo se separa y se filtra por longitud.
 */
private fun Product.buildSearchKeywords(): List<String> =
    "$description $businessName"
        .lowercase()
        .split(Regex("[^\\p{L}\\p{N}]+"))
        .filter { it.length >= 4 }
        .distinct()
        .take(40)