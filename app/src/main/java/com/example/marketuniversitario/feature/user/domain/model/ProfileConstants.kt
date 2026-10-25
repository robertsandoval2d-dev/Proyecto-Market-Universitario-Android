package com.example.marketuniversitario.feature.user.domain.model

import com.example.marketuniversitario.feature.business.domain.models.ProductCategory

object ProfileConstants {
    val faculties = listOf(
        "Ingeniería de Sistemas e Informática (FISI)",
        "Ingeniería Industrial (FII)",
        "Medicina Humana",
        "Letras y Ciencias Humanas",
        "Ciencias Biológicas",
        "Ciencias Contables",
        "Otra Facultad"
    )

    val genders = listOf(
        "Masculino",
        "Femenino",
        "Otro",
        "Prefiero no decirlo"
    )

    val intents = listOf(
        "Solo comprar",
        "Solo vender",
        "Ambos (Comprar y Vender)"
    )

    val defaultPreferences: List<ProductCategory> = listOf(
        ProductCategory.TECH_ELECTRONICS,
        ProductCategory.FOOD_BEVERAGES,
        ProductCategory.CLOTHING_MERCH,
        ProductCategory.ACADEMIC_MATERIAL,
        ProductCategory.SERVICES_TUTORING,
        ProductCategory.STATIONERY_SUPPLIES
    )
}