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
import java.util.concurrent.TimeUnit

@Serializable
data class PlantDTO(
    val id: Int? = null,
    val code: String? = null,
    val name: String,
    val common_synonyms: String? = null,
    val scientific_name: String? = null,
    val family: String? = null,
    val botanical_description: String? = null,
    val habitat: String? = null,
    val distribution: String? = null,
    val chemical_composition: String? = null,
    val toxicity: String? = null,
    val ethnomedicinal: String? = null,
    val employment_form: String? = null,
    val adverse_effects: String? = null,
    val other_uses: String? = null,
    val voucher: String? = null,
    val bibliography: String? = null,
    val image_url: String? = null,
    val category: String? = null
)

fun PlantDTO.toDomainModel(): Plant {
    return Plant(
        code = code ?: "UAC-IIAP",
        name = name,
        commonSynonyms = common_synonyms ?: "No disponible",
        scientificName = scientific_name ?: "No disponible",
        family = family ?: "No disponible",
        description = botanical_description ?: "Sin descripción",
        habitat = habitat ?: "No especificado",
        distribution = distribution ?: "No especificada",
        chemicalComposition = chemical_composition ?: "No disponible",
        toxicity = toxicity ?: "No presenta toxicidad",
        ethnomedicinal = ethnomedicinal ?: "No disponible",
        preparation = employment_form ?: "No disponible",
        adverseEffects = adverse_effects ?: "Ninguno",
        otherUses = other_uses ?: "No especificado",
        voucher = voucher ?: "No disponible",
        bibliography = bibliography ?: "No disponible",
        imageUrl = image_url ?: "https://via.placeholder.com/150",
        category = category ?: "General"
    )
}

interface ApiService {
    @GET("plants")
    suspend fun getPlants(): List<PlantDTO>
}

object RetrofitInstance {
    private const val BASE_URL = "https://plantas-medicinales-backend-bnjnty-c8c969-45-232-148-245.traefik.me/"

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }
    
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)
}
