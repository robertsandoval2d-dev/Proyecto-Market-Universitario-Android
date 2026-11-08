package com.example.marketuniversitario.core.vision_ai.onnx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import com.example.marketuniversitario.core.vision_ai.VisionAiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri

class OnnxVisionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val classifier: VisionOnnxClassifier,
    private val preprocessor: ImagePreprocessor,
    private val labels: ImageNetLabels
) : VisionAiService {

    override suspend fun extractTagsFromImage(imageUriString: String): List<String> =
        classifyImage(imageUriString, efficientNetB0Config)

    suspend fun classifyImage(
        imageUriString: String,
        model: OnnxModelConfig
    ): List<String> = withContext(Dispatchers.IO) {
        val uri = imageUriString.toUri()
        val bitmap = loadOrientedBitmap(uri)
        try {
            val input = preprocessor.toNchwFloatArray(bitmap, model.image)
            classifier.classify(
                inputData = input,
                labels = labels.load(model.labelsAssetPath),
                config = model,
                topK = 3
            ).map { it.substringBefore(',').trim() }.distinct()

        } finally {
            bitmap.recycle()
        }
    }

    private fun loadOrientedBitmap(uri: Uri): Bitmap {
        val orientation = context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "No se pudo abrir la imagen seleccionada" }
            ExifInterface(input).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        }

        val decoded = context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "No se pudo abrir la imagen seleccionada" }
            BitmapFactory.decodeStream(input)
                ?: error("No se pudo decodificar la imagen seleccionada")
        }

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return decoded
        }

        return Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, false)
            .also { if (it !== decoded) decoded.recycle() }
    }
}
