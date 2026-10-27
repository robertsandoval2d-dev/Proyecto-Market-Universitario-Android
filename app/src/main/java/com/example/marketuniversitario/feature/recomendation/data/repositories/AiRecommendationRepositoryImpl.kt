package com.example.marketuniversitario.feature.recomendation.data.repositories

import android.util.Log
import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.recomendation.domain.repositories.AiRecommendationRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class AiRecommendationRepositoryImpl @Inject constructor() : AiRecommendationRepository {

    private data class CacheEntry(val productIds: List<String>, val fetchTime: Long)

    // Caché por usuario, thread-safe (TTL fijo para limitar el consumo de tokens)
    private val userCache = ConcurrentHashMap<String, CacheEntry>()
    private val gson = Gson()

    // Backend Gemini Developer API + salida JSON forzada (array de strings)
    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.agentPlatform()).generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = Schema.array(Schema.string())
                temperature = 0.2f
                thinkingConfig = thinkingConfig { thinkingBudget = 0 }
            }
        )
    }

    override suspend fun getRankedProductIds(
        user: User,
        candidates: List<Product>
    ): Result<List<String>> {
        if (candidates.isEmpty()) return Result.success(emptyList())

        val userId = user.id.ifBlank { "default_user" }
        val now = System.currentTimeMillis()
        val pool = candidates.take(MAX_CANDIDATES)

        // Afinidad por producto: suma de los puntos de sus tags y category (la calcula el código, no el modelo)
        val affinityById = pool.associate { p ->
            p.id to (p.tags.sumOf { user.affinityScores[it] ?: 0 } + (user.affinityScores[p.category] ?: 0))
        }

        // Pool ordenado por afinidad (sortedByDescending es estable: los empates conservan el orden).
        // Sirve de fallback, para completar lo que la IA omita y como orden inicial que ve el modelo.
        val rankedPool = pool.sortedByDescending { affinityById[it.id] ?: 0 }
        val localOrder = rankedPool.map { it.id }

        // Caché: no consume tokens, pero se reconcilia con los candidatos actuales
        userCache[userId]?.let { entry ->
            if (now - entry.fetchTime < CACHE_TTL_MILLIS) {
                Log.d(TAG, "Usando caché de IA para usuario: $userId")
                return Result.success(reconcile(entry.productIds, localOrder))
            }
        }

        return try {
            val topAffinity = user.affinityScores.entries
                .sortedByDescending { it.value }
                .take(MAX_AFFINITY_TAGS)
            val preferences = user.preferences.distinct().take(MAX_PREFERENCES)
            // Asume que la búsqueda más reciente está al final de la lista
            val searches = user.recentSearches.takeLast(MAX_SEARCHES).reversed()

            val candidatesJson = gson.toJson(
                rankedPool.map {
                    mapOf(
                        "id" to it.id,
                        "name" to it.name,
                        "category" to it.category,
                        "tags" to it.tags,
                        "afinidad" to (affinityById[it.id] ?: 0)
                    )
                }
            )
            val signalsJson = gson.toJson(
                mapOf(
                    "afinidad_por_tag_o_categoria" to topAffinity.associate { it.key to it.value },
                    "preferencias_declaradas" to preferences,
                    "busquedas_recientes_mas_reciente_primero" to searches
                )
            )

            val weighting = if (topAffinity.isEmpty()) {
                """
                - El usuario es nuevo y no tiene historial: usa las preferencias declaradas como señal principal (~70%).
                - Las búsquedas recientes (~30%) reflejan lo que quiere ahora.
                """.trimIndent()
            } else {
                """
                - Cada producto trae su campo "afinidad", ya calculada (suma de los puntos de sus tags y de su categoría). No la recalcules: es la señal principal, así que ordena de mayor a menor afinidad.
                - Las búsquedas recientes (~30%) reflejan la intención actual: relaciónalas semánticamente con nombre, categoría y tags, y úsalas para subir un producto algunas posiciones. No deben pasar por encima de uno con afinidad claramente mayor.
                - Las preferencias declaradas solo sirven para desempatar.
                - Para dar variedad, puedes intercalar un producto de otra categoría entre varios de alta afinidad, pero nunca pongas uno con afinidad 0 o baja por encima de uno con afinidad alta sin una búsqueda que lo justifique.
                """.trimIndent()
            }

            val prompt = """
                Actúa como un motor de recomendación para un marketplace universitario.

                Señales del usuario (JSON; ignora las que vengan vacías): $signalsJson

                Cómo ponderarlas:
                $weighting

                Productos candidatos (JSON, ya ordenados por afinidad): $candidatesJson

                Devuelve un array JSON de strings con los IDs de los productos, ordenados del más al menos relevante.
                Usa únicamente IDs que existan en la lista de candidatos.
            """.trimIndent()

            Log.d(TAG, "Consultando $MODEL_NAME para usuario $userId (candidatos=${pool.size})")
            val response = model.generateContent(prompt)
            response.usageMetadata?.let {
                Log.d(
                    TAG,
                    "tokens → entrada=${it.promptTokenCount}, salida=${it.candidatesTokenCount}, razonamiento=${it.thoughtsTokenCount}"
                )
            }
            val responseText = response.text.orEmpty()
            Log.d(TAG, "Respuesta de Gemini: $responseText")

            val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
            val listType = object : TypeToken<List<String>>() {}.type
            val aiIds: List<String> = gson.fromJson(cleanJson, listType) ?: emptyList()

            val finalIds = reconcile(aiIds, localOrder)
            Log.d(TAG, "IDs devueltos por la IA=${aiIds.size}, finales=${finalIds.size}")

            userCache[userId] = CacheEntry(finalIds, now)
            Result.success(finalIds)
        } catch (e: CancellationException) {
            throw e // no tragarse la cancelación de corrutinas
        } catch (e: Exception) {
            Log.e(TAG, "Error al consultar Gemini (Firebase AI Logic), usando fallback", e)
            Result.success(localOrder) // no se cachea: el próximo intento vuelve a consultar
        }
    }

    // Descarta IDs inventados/repetidos/ya inexistentes y agrega al final los que falten
    private fun reconcile(rankedIds: List<String>, localOrder: List<String>): List<String> {
        val valid = localOrder.toSet()
        val ranked = rankedIds.filter { it in valid }.distinct()
        val rankedSet = ranked.toSet()
        return ranked + localOrder.filter { it !in rankedSet }
    }

    fun invalidateCache(userId: String) {
        userCache.remove(userId)
    }

    private companion object {
        const val TAG = "AiRecommendation"
        // Modelo estable actual según la documentación de Firebase AI Logic
        const val MODEL_NAME = "gemini-3.8-flash"
        const val CACHE_TTL_MILLIS = 30 * 60 * 1000L
        const val MAX_CANDIDATES = 100
        const val MAX_AFFINITY_TAGS = 15
        const val MAX_PREFERENCES = 8
        const val MAX_SEARCHES = 5
    }
}