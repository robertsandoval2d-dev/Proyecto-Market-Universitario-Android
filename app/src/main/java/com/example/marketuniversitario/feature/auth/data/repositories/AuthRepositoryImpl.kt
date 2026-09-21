package com.example.marketuniversitario.feature.auth.data.repositories

import com.example.marketuniversitario.feature.auth.data.mapper.AuthErrorMapper
import com.example.marketuniversitario.feature.auth.domain.models.UserSession
import com.example.marketuniversitario.feature.auth.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Result<UserSession> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.failure(Exception("No se encontró información del usuario."))
            Result.success(UserSession(
                uid = user.uid,
                email = user.email.orEmpty(),
                displayName = user.displayName,
                isEmailVerified = user.isEmailVerified
            ))
        } catch (e: Exception) {
            Result.failure(AuthErrorMapper.map(e))
        }
    }

    //Logeo o registro con cuenta de Google
    override suspend fun signInWithGoogleToken(idToken: String): Result<UserSession> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user ?: return Result.failure(Exception("No se encontró información del usuario."))

            Result.success(UserSession(
                uid = user.uid,
                email = user.email.orEmpty(),
                displayName = user.displayName,
                isEmailVerified = user.isEmailVerified
            ))
        } catch (e: Exception) {
            Result.failure(AuthErrorMapper.map(e))
        }
    }

    override suspend fun register(email: String, password: String): Result<UserSession> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: return Result.failure(Exception("No se encontró información del usuario."))
            Result.success(UserSession(
                uid = user.uid,
                email = user.email.orEmpty(),
                displayName = user.displayName,
                isEmailVerified = user.isEmailVerified
            ))
        } catch (e: Exception) {
            Result.failure(AuthErrorMapper.map(e))
        }
    }

    override suspend fun sendEmailVerification(): Result<Boolean> {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(AuthErrorMapper.map(e))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(AuthErrorMapper.map(e))
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified == true
    }

    override suspend fun reloadUser(): Result<UserSession> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No se encontró información del usuario."))
            user.reload().await()
            Result.success(
                UserSession(
                    uid = user.uid,
                    email = user.email.orEmpty(),
                    displayName = user.displayName,
                    isEmailVerified = user.isEmailVerified
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun logout() {
        firebaseAuth.signOut()
    }

}