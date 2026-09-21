package com.example.marketuniversitario.feature.user.domain.model

data class User (
    val id: String,
    val name: String="",
    val email: String,
    val phone: String="",
    val preferences: List<String> = emptyList(),
    val hasBusiness: Boolean = false,
    val businessId: String? = null
)