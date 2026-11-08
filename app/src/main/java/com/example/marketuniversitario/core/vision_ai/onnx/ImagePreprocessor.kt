package com.example.marketuniversitario.core.vision_ai.onnx

import android.graphics.Bitmap
import android.graphics.Color
import javax.inject.Inject
import javax.inject.Singleton

/** EfficientNet-B0 preprocessing: direct 224x224 nearest resize and validated RGB normalization. */
@Singleton
class ImagePreprocessor @Inject constructor() {
    fun toNchwFloatArray(source: Bitmap, config: ImagePreprocessorConfig): FloatArray {
        require(config.mean.size == 3 && config.std.size == 3) {
            "La configuración debe contener mean/std para RGB"
        }
        require(source.width > 0 && source.height > 0) { "Imagen vacía" }

        val size = config.inputSize
        val resized = resizeNearest(source, size, size)
        val planeSize = size * size
        val output = FloatArray(3 * planeSize)

        for (y in 0 until size) {
            for (x in 0 until size) {
                val pixel = resized[y * size + x]
                val channelValues = intArrayOf(
                    Color.red(pixel), Color.green(pixel), Color.blue(pixel)
                )
                val index = y * size + x
                for (channel in 0..2) {
                    val scaled = channelValues[channel] / 255f
                    val normalized = (scaled - config.mean[channel]) / config.std[channel]
                    output[channel * planeSize + index] = normalized
                }
            }
        }
        return output
    }

    private fun resizeNearest(source: Bitmap, outWidth: Int, outHeight: Int): IntArray {
        val output = IntArray(outWidth * outHeight)
        val sourceRow = IntArray(source.width)
        for (y in 0 until outHeight) {
            val sourceY = (y.toLong() * source.height / outHeight)
                .toInt()
                .coerceAtMost(source.height - 1)
            source.getPixels(sourceRow, 0, source.width, 0, sourceY, source.width, 1)
            for (x in 0 until outWidth) {
                val sourceX = (x.toLong() * source.width / outWidth)
                    .toInt()
                    .coerceAtMost(source.width - 1)
                output[y * outWidth + x] = sourceRow[sourceX]
            }
        }
        return output
    }
}
