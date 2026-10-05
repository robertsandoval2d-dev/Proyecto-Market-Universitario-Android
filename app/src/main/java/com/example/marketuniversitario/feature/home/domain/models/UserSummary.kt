package com.example.marketuniversitario.feature.home.domain.models

data class UserSummary(
    val id: String,
    val firstName: String = "",
    val photoUrl: String? = null
)
