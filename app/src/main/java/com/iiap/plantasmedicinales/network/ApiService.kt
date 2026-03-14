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
    val id: Int,
    val name: String,
    val scientific_name: String,
    val image_url: String,
    val category: String,
    val description: String,
    val benefits: String,
    val preparation: String,
    val ailments: String
)

// Mapeo de DTO a modelo de dominio
fun PlantDTO.toDomainModel(): Plant {
    return Plant(
        name = name,
        scientificName = scientific_name,
        imageUrl = image_url,
        category = category,
        description = description,
        benefits = benefits.split(","),
        preparation = preparation,
        ailments = ailments.split(",")
    )
}

interface ApiService {
    @GET("/plants")
    suspend fun getPlants(): List<PlantDTO>
}

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:3000" // Usamos esta IP para conectar al localhost del PC desde el emulador

    private val json = Json { ignoreUnknownKeys = true }

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

