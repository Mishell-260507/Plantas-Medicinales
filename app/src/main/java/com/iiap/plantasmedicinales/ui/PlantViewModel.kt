package com.iiap.plantasmedicinales.ui

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
import com.iiap.plantasmedicinales.data.Plant
import com.iiap.plantasmedicinales.data.PlantRepository
import com.iiap.plantasmedicinales.network.RetrofitInstance
import com.iiap.plantasmedicinales.network.toDomainModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val Context.dataStore by preferencesDataStore(name = "plantas_data")

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Lista dinámica de plantas (API + Local)
    private val _allPlants = mutableStateListOf<Plant>()
    val allPlants: List<Plant> = _allPlants

    private val _savedPlantNames = mutableStateListOf<String>()
    val savedPlantNames: List<String> = _savedPlantNames

    var userName by mutableStateOf("Explorador")
    var userEmail by mutableStateOf("")
    var lastSearchQuery by mutableStateOf("")

    private val SAVED_PLANTS_KEY = stringSetPreferencesKey("saved_plants_list")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val LAST_SEARCH_KEY = stringPreferencesKey("last_search")

    init {
        _allPlants.addAll(PlantRepository.allPlants)
        fetchPlantsFromApi()
        loadUserData()
    }

    private fun fetchPlantsFromApi() {
        viewModelScope.launch {
            try {
                val apiPlants = RetrofitInstance.api.getPlants()
                if (apiPlants.isNotEmpty()) {
                    _allPlants.clear()
                    _allPlants.addAll(apiPlants.map { it.toDomainModel() })
                }
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Error al conectar con la API: ${e.message}")
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collectLatest { preferences ->
                userName = preferences[USER_NAME_KEY] ?: "Explorador"
                userEmail = preferences[USER_EMAIL_KEY] ?: ""
                lastSearchQuery = preferences[LAST_SEARCH_KEY] ?: ""
                
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
            
            auth.currentUser?.uid?.let { uid ->
                try {
                    db.collection("users").document(uid).update(
                        mapOf(
                            "name" to name,
                            "email" to email
                        )
                    ).await()
                } catch (e: Exception) {
                    Log.e("FirebaseUpdate", "Error al actualizar en Firestore: ${e.message}")
                }
            }
        }
    }

    fun signUpAndSaveUser(
        name: String, 
        email: String, 
        pass: String, 
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Crear usuario en Firebase Authentication
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: throw Exception("No se pudo obtener el ID de usuario")

                // 2. Intentar guardar en Firestore, pero no bloquear el inicio si falla
                try {
                    val userMap = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("users").document(uid).set(userMap).await()
                } catch (e: Exception) {
                    Log.e("FirebaseFirestore", "Error al guardar perfil, pero el usuario se creó: ${e.message}")
                    // No lanzamos error aquí para permitir el inicio de sesión local
                }

                // 3. Guardar localmente en DataStore
                getApplication<Application>().dataStore.edit { preferences ->
                    preferences[USER_NAME_KEY] = name
                    preferences[USER_EMAIL_KEY] = email
                }
                
                userName = name
                userEmail = email
                onSuccess()

            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("email address is badly formatted") == true -> "Correo mal escrito"
                    e.message?.contains("already in use") == true -> "Este correo ya está registrado"
                    e.message?.contains("network error") == true -> "Error de red, revisa tu internet"
                    else -> "Error: ${e.localizedMessage}"
                }
                Log.e("FirebaseRegister", "Error: ${e.message}")
                onError(errorMsg)
            }
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
            auth.signOut()
            getApplication<Application>().dataStore.edit { it.clear() }
            userName = "Explorador"
            userEmail = ""
            lastSearchQuery = ""
        }
    }
}

