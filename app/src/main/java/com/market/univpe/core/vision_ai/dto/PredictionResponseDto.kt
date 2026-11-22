package com.market.univpe.core.vision_ai.dto

import com.google.gson.annotations.SerializedName

data class PredictionResponseDto(
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("success")
    val success: Boolean? = null
)