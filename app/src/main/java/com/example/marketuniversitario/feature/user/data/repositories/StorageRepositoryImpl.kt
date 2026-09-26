package com.example.marketuniversitario.feature.user.data.repositories

import android.net.Uri
import com.example.marketuniversitario.feature.user.domain.repositories.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadProfileImage(userId: String, imageUri: String): Result<String> {
        return try {
            val uri = Uri.parse(imageUri)
            val storageRef = storage.reference.child("profile_images/$userId.jpg")

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}