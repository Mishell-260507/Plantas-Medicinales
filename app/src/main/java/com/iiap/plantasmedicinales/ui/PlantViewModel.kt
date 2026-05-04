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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val Context.dataStore by preferencesDataStore(name = "plantas_data")

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _allPlants = mutableStateListOf<Plant>()
    val allPlants: List<Plant> = _allPlants

    private val _savedPlantNames = mutableStateListOf<String>()
    val savedPlantNames: List<String> = _savedPlantNames

    var userName by mutableStateOf("Explorador")
    var userEmail by mutableStateOf("")
    var lastSearchQuery by mutableStateOf("")
    
    var selectedCategory by mutableStateOf("Todas")

    private val SAVED_PLANTS_KEY = stringSetPreferencesKey("saved_plants_list")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val LAST_SEARCH_KEY = stringPreferencesKey("last_search")

    init {
        _allPlants.addAll(PlantRepository.allPlants)
        fetchPlantsFromApi()
        loadUserData()
        refreshUserProfileAndFavorites()
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
                Log.e("PlantViewModel", "Error API: ${e.message}")
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getApplication<Application>().dataStore.data.collectLatest { preferences ->
                val savedName = preferences[USER_NAME_KEY]
                if (!savedName.isNullOrBlank() && savedName != "Explorador") {
                    userName = savedName
                }
                userEmail = preferences[USER_EMAIL_KEY] ?: ""
                lastSearchQuery = preferences[LAST_SEARCH_KEY] ?: ""
                
                val plants = preferences[SAVED_PLANTS_KEY] ?: emptySet()
                _savedPlantNames.clear()
                _savedPlantNames.addAll(plants)
            }
        }
    }

    private fun refreshUserProfileAndFavorites() {
        val currentUser = auth.currentUser ?: return
        
        // Carga inicial desde el perfil de Auth (rápida)
        if (!currentUser.displayName.isNullOrBlank()) {
            userName = currentUser.displayName!!
        }
        userEmail = currentUser.email ?: ""

        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUser.uid).get().await()
                if (userDoc.exists()) {
                    val name = userDoc.getString("name") ?: userName
                    val favorites = (userDoc.get("favorites") as? List<String>)?.toSet() ?: emptySet()
                    
                    userName = name
                    getApplication<Application>().dataStore.edit { prefs ->
                        prefs[USER_NAME_KEY] = name
                        prefs[SAVED_PLANTS_KEY] = favorites
                    }
                }
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Sync Error: ${e.message}")
            }
        }
    }

    fun saveUser(name: String, email: String) {
        viewModelScope.launch {
            val cleanEmail = email.trim()
            userName = name
            userEmail = cleanEmail
            getApplication<Application>().dataStore.edit { 
                it[USER_NAME_KEY] = name
                it[USER_EMAIL_KEY] = cleanEmail 
            }
            
            auth.currentUser?.let { user ->
                try {
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    user.updateProfile(profileUpdates).await()
                    db.collection("users").document(user.uid).set(
                        mapOf("name" to name, "email" to cleanEmail),
                        SetOptions.merge()
                    ).await()
                } catch (e: Exception) {
                    Log.e("FirebaseUpdate", "Error: ${e.message}")
                }
            }
        }
    }

    fun signIn(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email.trim(), pass).await()
                val user = result.user ?: throw Exception("User null")
                
                userName = user.displayName ?: "Explorador"
                userEmail = user.email ?: email.trim()
                
                refreshUserProfileAndFavorites()
                onSuccess()
            } catch (e: Exception) {
                onError("Correo o contraseña incorrectos.")
            }
        }
    }

    fun signUpAndSaveUser(name: String, email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val cleanEmail = email.trim()
                val result = auth.createUserWithEmailAndPassword(cleanEmail, pass).await()
                val user = result.user ?: throw Exception("Error al crear cuenta")
                
                // Guardar nombre en el perfil de Auth inmediatamente
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                user.updateProfile(profileUpdates).await()
                
                userName = name
                userEmail = cleanEmail

                // Intentar guardar en Firestore (si falla aquí por reglas, no bloqueamos el éxito)
                try {
                    val userMap = hashMapOf(
                        "uid" to user.uid, 
                        "name" to name, 
                        "email" to cleanEmail, 
                        "favorites" to emptyList<String>()
                    )
                    db.collection("users").document(user.uid).set(userMap).await()
                } catch (e: Exception) {
                    Log.e("FirestoreCreate", "Fallo al crear doc, se creará al guardar favoritos")
                }

                getApplication<Application>().dataStore.edit { 
                    it[USER_NAME_KEY] = name
                    it[USER_EMAIL_KEY] = cleanEmail
                    it[SAVED_PLANTS_KEY] = emptySet()
                }
                onSuccess()
            } catch (e: Exception) {
                val msg = if (e.message?.contains("already in use") == true) "Este correo ya está registrado" else e.localizedMessage
                onError(msg ?: "Error al registrar")
            }
        }
    }

    fun saveLastSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            lastSearchQuery = query
            getApplication<Application>().dataStore.edit { it[LAST_SEARCH_KEY] = query }
        }
    }

    fun toggleSavePlant(plantName: String) {
        viewModelScope.launch {
            var updatedFavorites: Set<String> = emptySet()
            
            getApplication<Application>().dataStore.edit { preferences ->
                val currentSet = preferences[SAVED_PLANTS_KEY]?.toMutableSet() ?: mutableSetOf()
                if (currentSet.contains(plantName)) currentSet.remove(plantName) else currentSet.add(plantName)
                preferences[SAVED_PLANTS_KEY] = currentSet
                updatedFavorites = currentSet
            }
            
            auth.currentUser?.let { user ->
                try {
                    db.collection("users").document(user.uid).set(
                        mapOf("favorites" to updatedFavorites.toList(), "name" to userName, "email" to userEmail),
                        SetOptions.merge()
                    ).await()
                } catch (e: Exception) {
                    Log.e("FirestoreSync", "Error: ${e.message}")
                }
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
            _savedPlantNames.clear()
        }
    }
}
