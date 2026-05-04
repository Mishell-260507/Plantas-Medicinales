package com.iiap.plantasmedicinales.network

import com.iiap.plantasmedicinales.data.Plant
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

@Serializable
data class PlantDTO(
    val id: Int? = null,
    val name: String,
    val scientific_name: String? = null,
    val image_url: String? = null,
    val category: String? = null,
    val description: String? = null,
    val benefits: String? = null,
    val preparation: String? = null,
    val ailments: String? = null,
    val habitat: String? = null,
    val contraindications: String? = null,
    val toxicity_level: String? = null,
    val conservation_status: String? = null
)

// Mapeo robusto de DTO a modelo de dominio para no romper la UI
fun PlantDTO.toDomainModel(): Plant {
    return Plant(
        name = name,
        scientificName = scientific_name ?: "Nombre científico no disponible",
        imageUrl = image_url ?: "https://via.placeholder.com/150",
        category = category ?: "General",
        description = description ?: "Sin descripción disponible",
        benefits = benefits?.split(",")?.map { it.trim() } ?: emptyList(),
        preparation = preparation ?: "Información de preparación no disponible",
        ailments = ailments?.split(",")?.map { it.trim() } ?: emptyList(),
        habitat = habitat ?: "No especificado",
        contraindications = contraindications ?: "Sin contraindicaciones registradas",
        toxicityLevel = toxicity_level ?: "Bajo",
        conservationStatus = conservation_status ?: "Preocupación menor"
    )
}

interface ApiService {
    @GET("plants")
    suspend fun getPlants(): List<PlantDTO>
}

object RetrofitInstance {
    // NUEVA URL DE LA API
    private const val BASE_URL = "https://plantas-medicSinales-backend-bnjnty-c8c969-45-232-148-245.traefik.me/"

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)
}
