package com.example.marketuniversitario.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

fun compressImageToWebP(
    context: Context,
    imageUri: Uri,
    maxWidth: Int = 1080,
    maxHeight: Int = 1080,
    quality: Int = 80
): ByteArray? { // Retorna ByteArray? por si la imagen está corrupta

    // 1. Leer solo las dimensiones de forma segura usando .use {}
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream, null, options)
    }

    // Calcular escala
    val width = options.outWidth
    val height = options.outHeight
    if (width <= 0 || height <= 0) return null // Imagen no válida o corrupta

    var scale = 1
    if (width > maxWidth || height > maxHeight) {
        val widthRatio = (width.toFloat() / maxWidth).roundToInt()
        val heightRatio = (height.toFloat() / maxHeight).roundToInt()
        // Elegimos el ratio mayor para garantizar que encaje en 1080x1080
        scale = max(widthRatio, heightRatio)
    }

    // 2. Cargar la imagen ya reducida en memoria
    val decodeOptions = BitmapFactory.Options().apply { inSampleSize = scale }
    val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream, null, decodeOptions)
    } ?: return null

    // 3. Ajuste fino de dimensiones si es necesario
    val finalBitmap = if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
        val ratio = minOf(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
        val targetWidth = (bitmap.width * ratio).toInt()
        val targetHeight = (bitmap.height * ratio).toInt()

        Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true).also {
            if (it != bitmap) bitmap.recycle() // Libera el bitmap viejo
        }
    } else {
        bitmap
    }

    // 4. Comprimir a WebP
    val outputStream = ByteArrayOutputStream()
    val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        @Suppress("DEPRECATION")
        Bitmap.CompressFormat.WEBP
    }

    finalBitmap.compress(format, quality, outputStream)
    finalBitmap.recycle() // Libera el final

    return outputStream.toByteArray()
}
