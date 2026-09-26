package com.example.marketuniversitario.feature.user.domain.model

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

    val defaultPreferences = listOf(
        "Tecnología y Electrónica",
        "Tutorías Académicas",
        "Comida y Snacks",
        "Materiales y Libros",
        "Ropa y Accesorios",
        "Servicios Varios"
    )
}