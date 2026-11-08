package com.example.marketuniversitario.core.vision_ai.onnx

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.read
import kotlin.concurrent.write

/** One inference engine that caches an ONNX session for each configured model. */
@Singleton
class VisionOnnxClassifier @Inject constructor(
    @ApplicationContext context: Context
) : AutoCloseable {
    private val context = context.applicationContext
    private val environment = OrtEnvironment.getEnvironment()
    private val sessions = ConcurrentHashMap<String, OrtSession>()
    private val lifecycleLock = ReentrantReadWriteLock()
    private var closed = false

    private fun sessionFor(config: OnnxModelConfig): OrtSession =
        sessions.computeIfAbsent(config.modelAssetPath) {
            val modelFile = copyModelToCache(config.modelAssetPath)
            OrtSession.SessionOptions().use { options ->
                environment.createSession(modelFile.absolutePath, options)
            }.also { session ->
                Log.d(TAG, "Loaded ${config.modelAssetPath}; inputs=${session.inputInfo}, outputs=${session.outputInfo}")
            }
        }

    private fun copyModelToCache(assetPath: String): File {
        val fileName = assetPath.substringAfterLast('/')
        val modelFile = File(context.cacheDir, fileName)
        if (!modelFile.exists() || modelFile.length() == 0L) {
            context.assets.open(assetPath).use { input ->
                modelFile.outputStream().use { output -> input.copyTo(output) }
            }
        }
        return modelFile
    }

    fun inspectModel(
        config: OnnxModelConfig = efficientNetB0Config
    ) {
        lifecycleLock.read {
            checkNotClosed()
            val session = sessionFor(config)
            Log.d(TAG, "Inspected ${config.modelAssetPath}; inputs=${session.inputInfo}, outputs=${session.outputInfo}")
        }
    }

    fun closeModel(config: OnnxModelConfig) {
        lifecycleLock.write {
            sessions.remove(config.modelAssetPath)?.close()
        }
    }

    override fun close() {
        lifecycleLock.write {
            if (closed) return@write
            closed = true

            val closeErrors = mutableListOf<Exception>()
            sessions.values.forEach { session ->
                try {
                    session.close()
                } catch (error: Exception) {
                    closeErrors += error
                }
            }
            sessions.clear()

            if (closeErrors.isNotEmpty()) {
                val failure = IllegalStateException("No se pudieron cerrar todas las sesiones ONNX", closeErrors.first())
                closeErrors.drop(1).forEach(failure::addSuppressed)
                throw failure
            }
        }
    }

    fun classify(
        inputData: FloatArray,
        labels: List<String>,
        config: OnnxModelConfig,
        topK: Int = 3
    ): List<String> = lifecycleLock.read {
        checkNotClosed()
        val session = sessionFor(config)
        val inputName = config.inputName
        require(session.inputNames.contains(inputName)) {
            "El modelo ${config.modelAssetPath} no contiene la entrada '${config.inputName}'. Entradas: ${session.inputNames}"
        }

        val inputShape = (session.inputInfo[inputName]?.info as? TensorInfo)?.shape
            ?: error("No se pudo leer la forma de entrada de ${config.modelAssetPath}")
        val size = config.image.inputSize
        val expectedShape = longArrayOf(1, 3, size.toLong(), size.toLong())
        val shapeIsCompatible = inputShape.size == expectedShape.size &&
            inputShape.indices.all { index ->
                inputShape[index] == -1L || inputShape[index] == expectedShape[index]
            }
        require(shapeIsCompatible) {
            "El modelo espera ${inputShape.contentToString()}; el preprocesador genera [1, 3, $size, $size]"
        }
        require(inputData.size == 3 * size * size) {
            "Tensor inválido: ${inputData.size} valores; se esperaban ${3 * size * size}"
        }

        val inputBuffer = ByteBuffer
            .allocateDirect(inputData.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(inputData)
            .apply { rewind() }

        OnnxTensor.createTensor(
            environment,
            inputBuffer,
            longArrayOf(1, 3, size.toLong(), size.toLong())
        ).use { tensor ->
            session.run(mapOf(inputName to tensor)).use { result ->
                val scores = flattenScores(result[0].value)
                require(labels.size == scores.size) {
                    "El modelo devolvió ${scores.size} salidas, pero hay ${labels.size} etiquetas"
                }
                scores.indices
                    .sortedByDescending { scores[it] }
                    .take(topK.coerceAtMost(scores.size))
                    .map { index ->
                        Log.d(TAG, "${config.modelAssetPath}: ${labels[index]} (${scores[index]})")
                        labels[index]
                    }
            }
        }
    }

    private fun checkNotClosed() {
        check(!closed) { "VisionOnnxClassifier ya fue cerrado" }
    }

    private fun flattenScores(value: Any): FloatArray = when (value) {
        is FloatArray -> value
        is Array<*> -> flattenScores(value.firstOrNull() ?: error("Salida ONNX vacía"))
        else -> error("Tipo de salida ONNX no compatible: ${value.javaClass.name}")
    }

    companion object {
        private const val TAG = "VisionONNX"
    }
}
