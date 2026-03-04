package com.example.plantasmedicinales.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantasmedicinales.data.PlantRepository
import com.example.plantasmedicinales.ui.theme.PlantasMedicinalesTheme

@Composable
fun SearchScreen(onPlantClick: (String) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    val allPlants = PlantRepository.allPlants
    
    val filteredResults = if (query.isEmpty()) {
        emptyList()
    } else {
        allPlants.filter { plant ->
            plant.name.contains(query, ignoreCase = true) || 
            plant.scientificName.contains(query, ignoreCase = true) ||
            plant.ailments.any { it.contains(query, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Explora y Busca",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Text(
            text = "Encuentra el remedio perfecto por síntoma o nombre",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Ej: Tos, Gripe, Manzanilla...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (query.isEmpty()) {
            PopularSuggestions(onSuggestionClick = { query = it })
        } else if (filteredResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.AutoMirrored.Filled.ManageSearch, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text(text = "No se encontraron resultados", color = Color.Gray)
                }
            }
        } else {
            Text(
                text = "Resultados (${filteredResults.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredResults) { plant ->
                    SearchResultCard(plant.name, plant.scientificName, onPlantClick)
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(name: String, scientific: String, onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick(name) },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1B5E20))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = scientific, fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        }
    }
}

@Composable
fun PopularSuggestions(onSuggestionClick: (String) -> Unit) {
    Column {
        Text(text = "Sugerencias populares", fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(16.dp))
        val tags = listOf("Gripe", "Gastritis", "Insomnio", "Heridas")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { onSuggestionClick(tag) }
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    PlantasMedicinalesTheme {
        SearchScreen()
    }
}
