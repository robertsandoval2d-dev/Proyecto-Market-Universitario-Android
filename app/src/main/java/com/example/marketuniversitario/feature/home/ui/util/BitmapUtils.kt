package com.example.marketuniversitario.feature.home.ui.util
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(
        context.cacheDir,
        "camera_photo_${System.currentTimeMillis()}.jpg"
    )

    file.outputStream().use { out ->
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            90,
            out
        )
    }

    return Uri.fromFile(file)
}