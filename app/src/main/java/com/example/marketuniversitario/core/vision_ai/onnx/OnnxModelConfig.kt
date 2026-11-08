package com.example.marketuniversitario.core.vision_ai.onnx

data class ImagePreprocessorConfig(
    val inputSize: Int,
    val mean: FloatArray,
    val std: FloatArray
)

data class OnnxModelConfig(
    val modelAssetPath: String,
    val labelsAssetPath: String,
    val image: ImagePreprocessorConfig,
    val inputName: String = "pixel_values"
)

internal val efficientNetB0Config = OnnxModelConfig(
    modelAssetPath = "models/efficientnet_b0.onnx",
    labelsAssetPath = "models/efficientnet_b0_labels.json",
    image = ImagePreprocessorConfig(
        inputSize = 224,
        mean = floatArrayOf(0.485f, 0.456f, 0.406f),
        std = floatArrayOf(0.47853944f, 0.4732864f, 0.47434163f)
    )
)
