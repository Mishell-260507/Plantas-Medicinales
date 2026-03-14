package com.iiap.plantasmedicinales

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.iiap.plantasmedicinales.ui.NavGraph
import com.iiap.plantasmedicinales.ui.theme.PlantasMedicinalesTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    // Definimos las variables para Auth y Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos Firebase usando la sintaxis moderna
        auth = Firebase.auth
        db = Firebase.firestore

        // Prueba rápida: ¿Está bien conectado?
        val user = auth.currentUser
        if (user == null) {
            Log.d("FirebaseTest", "Firebase conectado correctamente: Listo para autenticar")
        } else {
            Log.d("FirebaseTest", "Usuario ya conectado: ${user.email}")
        }

        enableEdgeToEdge()
        setContent {
            PlantasMedicinalesTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

