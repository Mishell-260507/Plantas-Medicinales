package com.iiap.plantasmedicinales.data

import kotlinx.serialization.Serializable

@Serializable
data class Family(
    val id: Int,
    val name: String
)

@Serializable
data class Plant(
    val id: Int = 0,
    val code: String? = null,
    val commonNames: String? = null,
    val synonyms: String? = null,
    val scientificName: String? = null,
    val family: Family? = null,
    val scientificDescription: String? = null,
    val habitat: String? = null,
    val distribution: String? = null,
    val chemicalComposition: String? = null,
    val toxicity: String? = null,
    val ethnomedicinalUses: String? = null,
    val management: String? = null,
    val interactions: String? = null,
    val otherUses: String? = null,
    val vaucher: String? = null,
    val bibliographicReferences: String? = null,
    val imageUrl: String? = null,
    val status: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val category: String = "General"
) {
    // Getters auxiliares para facilitar el uso en la UI
    val name: String get() = commonNames ?: "Sin nombre"
    val description: String get() = scientificDescription ?: "Sin descripción"
    val preparation: String get() = management ?: "No disponible"
    val ethnomedicinal: String get() = ethnomedicinalUses ?: "No disponible"

    val fullImageUrl: String? get() {
        val baseUrl = "https://qa-api-plantas.iiap.gob.pe"
        if (imageUrl.isNullOrBlank()) return null
        // Si ya es una URL completa, devolverla
        if (imageUrl.startsWith("http")) return imageUrl
        // Si empieza con /, concatenar. Si no, añadir / al inicio.
        val path = if (imageUrl.startsWith("/")) imageUrl else "/$imageUrl"
        return "$baseUrl$path"
    }
}
