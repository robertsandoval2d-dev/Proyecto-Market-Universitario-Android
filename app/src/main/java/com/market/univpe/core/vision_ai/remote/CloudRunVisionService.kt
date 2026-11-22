package com.market.univpe.core.vision_ai.remote

import android.content.Context
import android.net.Uri
import com.market.univpe.core.network.util.NetworkResult
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

class CloudRunVisionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: VisionCloudRunApiService
) {
    suspend fun extractTagsFromImage(imageUri: Uri): NetworkResult<List<String>> {
        val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: throw IllegalArgumentException("No se pudo leer la imagen")

        val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", "imagen.jpg", requestFile)

        return try {
            val response = apiService.predictImage(body)

            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.tags)
            } else {
                NetworkResult.Error("Error en Cloud Run (${response.code()})", response.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Sin conexión a internet. Verifica tu red.")
        } catch (e: Exception) {
            NetworkResult.Error("Error desconocido: ${e.localizedMessage}")
        }
    }
}