package com.example.plantasmedicinales.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantasmedicinales.ui.theme.PlantasMedicinalesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(plantName: String, onBack: () -> Unit) {
    // Mock data based on the name
    val scientificName = when(plantName) {
        "Uña de Gato" -> "Uncaria tomentosa"
        "Sangre de Grado" -> "Croton lechleri"
        "Matico" -> "Buddleja globosa"
        "Achiote" -> "Bixa orellana"
        else -> "Planta medicinal"
    }
    
    val bgColor = when(plantName) {
        "Uña de Gato" -> Color(0xFF4E342E)
        "Sangre de Grado" -> Color(0xFF3E2723)
        "Matico" -> Color(0xFF1B5E20)
        "Achiote" -> Color(0xFFBF360C)
        else -> Color.Gray
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Planta", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorito")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(bgColor, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Image placeholder
                Text(text = plantName, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = plantName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = scientificName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta es una planta medicinal ampliamente utilizada en la medicina tradicional por sus propiedades curativas. Ayuda en el tratamiento de diversas dolencias comunes.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Beneficios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                BulletPoint("Fortalece el sistema inmunológico")
                BulletPoint("Propiedades antiinflamatorias")
                BulletPoint("Auxiliar en problemas digestivos")
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(6.dp).background(Color(0xFF00C853), RoundedCornerShape(3.dp)))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PlantDetailScreenPreview() {
    PlantasMedicinalesTheme {
        PlantDetailScreen("Matico", onBack = {})
    }
}
