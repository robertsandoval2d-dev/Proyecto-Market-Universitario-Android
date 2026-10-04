package com.example.marketuniversitario.feature.business.data.datasources

import android.content.Context
import android.net.Uri
import com.example.marketuniversitario.core.util.compressImageToWebP
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessStorageRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage
) {
    suspend fun uploadImage(path: String, imageUriString: String): String =
        withContext(Dispatchers.IO) { // 1. Garantizamos que TODO esto ocurra en segundo plano

            val uri = Uri.parse(imageUriString)

            // 2. Comprimir la imagen. Si devuelve null, lanzamos un error claro.
            val compressedBytes = compressImageToWebP(context, uri)
                ?: throw IllegalArgumentException("No se pudo leer o comprimir la imagen de la galería.")

            // 3. Crear la referencia en Firebase con la ruta indicada
            val storageRef = storage.reference.child(path)

            // 4. Subir los bytes comprimidos
            storageRef.putBytes(compressedBytes).await()

            // 5. Obtener la URL pública y retornarla
            val downloadUrl = storageRef.downloadUrl.await()

            return@withContext downloadUrl.toString()
        }
}