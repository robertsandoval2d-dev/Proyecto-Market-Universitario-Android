package com.example.marketuniversitario.feature.recomendation.data.repositories

import android.util.Log
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.example.marketuniversitario.feature.recomendation.domain.repositories.InteractionRepository
import com.example.marketuniversitario.feature.user.domain.model.User
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InteractionRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : InteractionRepository {
    override suspend fun incrementAffinityScores(userId: String, tags: List<String>, weight: Int): Result<Unit> {
        return try {
            val validTags = tags.filter { it.isNotBlank() }
            if (validTags.isEmpty()) return Result.success(Unit)

            val currentUserId = authRepository.getCurrentUserId()

            if (userId == currentUserId) {
                // Caso 1: El usuario actualiza su propio perfil (ej. VIEW) -> Offline-First (Room + WorkManager)
                val user = userRepository.getUser(userId).getOrNull() ?: User(
                    id = userId,
                    email = ""
                )

                val currentScores = user.affinityScores.toMutableMap()
                validTags.forEach { tag ->
                    val currentWeight = currentScores[tag] ?: 0
                    currentScores[tag] = currentWeight + weight
                }

                val updatedUser = user.copy(affinityScores = currentScores)
                userRepository.saveUser(updatedUser)
            } else {
                // Caso 2: Un vendedor actualiza el perfil de otro usuario (ej. PURCHASE al completar una orden).
                // Si estamos sin internet, capturamos la excepción para que el vendedor pueda completar
                // la orden offline sin que la app falle. (En producción, una Cloud Function en Firestore
                // se encargaría de recalcular puntajes al sincronizar la orden).
                try {
                    val userRef = firestore.collection("users").document(userId)
                    val updates = mutableMapOf<String, Any>()
                    validTags.forEach { tag ->
                        updates["affinityScores.$tag"] = FieldValue.increment(weight.toLong())
                    }
                    userRef.update(updates).await()
                } catch (e: Exception) {
                    Log.w("InteractionRepo", "No se pudo actualizar affinityScores de otro usuario (offline mode).", e)
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
