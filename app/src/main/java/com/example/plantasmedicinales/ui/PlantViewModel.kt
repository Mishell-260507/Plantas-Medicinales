package com.example.plantasmedicinales.ui

import android.app.Application
import android.content.Context
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "plantas_data")

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    private val _savedPlantNames = mutableStateListOf<String>()
    val savedPlantNames: List<String> = _savedPlantNames

    // Usamos variables observables directas para el usuario
    var userName by mutableStateOf("Usuario")
    var userEmail by mutableStateOf("")

    private val SAVED_PLANTS_KEY = stringSetPreferencesKey("saved_plants_list")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collectLatest { preferences ->
                userName = preferences[USER_NAME_KEY] ?: "Explorador"
                userEmail = preferences[USER_EMAIL_KEY] ?: "sin correo"
                
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
            // Forzamos la actualización inmediata del estado local
            userName = name
            userEmail = email
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

    fun getSavedPlants(): List<Plant> = PlantRepository.allPlants.filter { _savedPlantNames.contains(it.name) }

    fun logout() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { it.clear() }
            userName = "Explorador"
            userEmail = ""
        }
    }
}
