package com.iiap.plantasmedicinales.util

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.InputStreamReader

object TranslationManager {
    private var translations: Map<String, String> = emptyMap()
    private var currentLanguage = "es"

    fun loadTranslations(context: Context, lang: String = "es") {
        currentLanguage = lang
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("translations/$lang.json")
            val reader = InputStreamReader(inputStream)
            val jsonString = reader.readText()
            val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
            translations = jsonObject.mapValues { it.value.jsonPrimitive.content }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getString(key: String): String {
        return translations[key] ?: key
    }
}
