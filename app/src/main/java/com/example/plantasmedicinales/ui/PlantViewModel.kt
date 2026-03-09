package com.example.plantasmedicinales.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantasmedicinales.data.Plant
import com.example.plantasmedicinales.data.PlantRepository
import com.example.plantasmedicinales.network.RetrofitInstance
import com.example.plantasmedicinales.network.toDomainModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "plantas_data")

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    
    // Lista dinámica de plantas (API + Local)
    private val _allPlants = mutableStateListOf<Plant>()
    val allPlants: List<Plant> = _allPlants

    private val _savedPlantNames = mutableStateListOf<String>()
    val savedPlantNames: List<String> = _savedPlantNames

    var userName by mutableStateOf("Explorador")
    var userEmail by mutableStateOf("")
    var lastSearchQuery by mutableStateOf("") // Nueva variable para la última búsqueda

    private val SAVED_PLANTS_KEY = stringSetPreferencesKey("saved_plants_list")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val LAST_SEARCH_KEY = stringPreferencesKey("last_search") // Nueva clave para DataStore

    init {
        // 1. Cargar plantas locales por defecto para que la UI no esté vacía
        _allPlants.addAll(PlantRepository.allPlants)
        
        // 2. Intentar actualizar desde la API
        fetchPlantsFromApi()
        
        // 3. Cargar datos de usuario, favoritos y última búsqueda
        loadUserData()
    }

    private fun fetchPlantsFromApi() {
        viewModelScope.launch {
            try {
                val apiPlants = RetrofitInstance.api.getPlants()
                if (apiPlants.isNotEmpty()) {
                    _allPlants.clear()
                    _allPlants.addAll(apiPlants.map { it.toDomainModel() })
                    Log.d("PlantViewModel", "Datos cargados desde la API con éxito")
                }
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Error al conectar con la API: ${e.message}. Usando datos locales.")
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collectLatest { preferences ->
                userName = preferences[USER_NAME_KEY] ?: "Explorador"
                userEmail = preferences[USER_EMAIL_KEY] ?: ""
                lastSearchQuery = preferences[LAST_SEARCH_KEY] ?: "" // Cargamos la última búsqueda
                
                val plants = preferences[SAVED_PLANTS_KEY] ?: emptySet()
                _savedPlantNames.clear()
                _savedPlantNames.addAll(plants)
            }
        }
    }

    fun saveUser(name: String, email: String) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[USER_NAME_KEY] = name
                preferences[USER_EMAIL_KEY] = email
            }
            userName = name
            userEmail = email
        }
    }

    fun saveLastSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[LAST_SEARCH_KEY] = query
            }
            lastSearchQuery = query
        }
    }

    fun toggleSavePlant(plantName: String) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                val currentSet = preferences[SAVED_PLANTS_KEY]?.toMutableSet() ?: mutableSetOf()
                if (currentSet.contains(plantName)) {
                    currentSet.remove(plantName)
                } else {
                    currentSet.add(plantName)
                }
                preferences[SAVED_PLANTS_KEY] = currentSet
            }
        }
    }

    fun isPlantSaved(plantName: String): Boolean = _savedPlantNames.contains(plantName)

    fun getSavedPlants(): List<Plant> = _allPlants.filter { _savedPlantNames.contains(it.name) }

    fun logout() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { it.clear() }
            userName = "Explorador"
            userEmail = ""
            lastSearchQuery = ""
        }
    }
}
