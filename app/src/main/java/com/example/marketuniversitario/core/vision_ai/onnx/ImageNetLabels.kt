package com.example.marketuniversitario.core.vision_ai.onnx

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageNetLabels @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cache = ConcurrentHashMap<String, List<String>>()

    fun load(assetPath: String): List<String> = cache.getOrPut(assetPath) {
        val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        List(array.length()) { index -> array.getString(index).trim() }
    }
}
