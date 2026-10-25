package com.example.marketuniversitario.feature.recomendation.data.repositories

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.recomendation.domain.repositories.AiRecommendationRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class AiRecommendationRepositoryImpl @Inject constructor() : AiRecommendationRepository {
    override suspend fun getRankedProductIds(user: User, candidates: List<Product>): Result<List<String>> {
        return try {
            val vertexAI = Firebase.vertexAI
            val model = vertexAI.generativeModel("gemini-3.8-flash")

            // Extraer los 5 tags con mayor puntaje de affinityScores
            val topTags = user.affinityScores.entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key }

            val combinedPreferences = (topTags + user.recentSearches).distinct()

            val candidatesJson = candidates.map { mapOf("id" to it.id, "name" to it.name, "category" to it.category, "tags" to it.tags) }
            val prompt = """
                Actúa como un motor de recomendación para un marketplace universitario.
                Preferencias del usuario (tags y búsquedas recientes): $combinedPreferences.
                Lista de productos candidatos en JSON: $candidatesJson.
                Devuelve únicamente una lista JSON ordenada con los IDs de los productos más relevantes para el usuario según sus preferencias, sin texto adicional.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val responseText = response.text.orEmpty()

            // 1. Limpiar el formato Markdown de Gemini
            val cleanJson = responseText.replace("```json", "").replace("```", "").trim()

            // 2. Deserializar el texto JSON a una lista de Kotlin
            val listType = object : TypeToken<List<String>>() {}.type
            val productIds: List<String> = Gson().fromJson(cleanJson, listType)

            Result.success(productIds)
        } catch (e: Exception) {
            Result.success(candidates.map { it.id })
        }
    }
}
