package com.iiap.plantasmedicinales.data

import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    val code: String = "",
    val name: String,
    val commonSynonyms: String = "",
    val scientificName: String,
    val family: String = "",
    val description: String,
    val habitat: String = "",
    val distribution: String = "",
    val chemicalComposition: String = "",
    val toxicity: String = "",
    val ethnomedicinal: String = "",
    val preparation: String, // Forma de empleo
    val adverseEffects: String = "",
    val otherUses: String = "",
    val voucher: String = "",
    val bibliography: String = "",
    val imageUrl: String,
    val category: String = "General"
)
