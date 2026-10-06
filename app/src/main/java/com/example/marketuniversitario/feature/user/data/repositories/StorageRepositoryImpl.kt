package com.example.marketuniversitario.feature.user.data.repositories

import android.content.Context
import android.net.Uri
import com.example.marketuniversitario.core.util.compressImageToWebP
import com.example.marketuniversitario.feature.user.domain.repositories.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val uri = Uri.parse(imageUri)
                val compressedBytes = compressImageToWebP(context, uri)
                    ?: throw IllegalArgumentException("No se pudo leer o comprimir la imagen de la galería.")
                val storageRef = storage.reference.child("profile_images/$userId.webp")

                storageRef.putBytes(compressedBytes).await()
                val downloadUrl = storageRef.downloadUrl.await()

                Result.success(downloadUrl.toString())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}