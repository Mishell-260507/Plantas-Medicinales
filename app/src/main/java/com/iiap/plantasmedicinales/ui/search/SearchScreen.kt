package com.iiap.plantasmedicinales.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iiap.plantasmedicinales.data.Plant
import com.iiap.plantasmedicinales.ui.PlantViewModel
import com.iiap.plantasmedicinales.util.TranslationManager

@Composable
fun SearchScreen(viewModel: PlantViewModel, onPlantClick: (String) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    val t = TranslationManager
    val allPlants = viewModel.allPlants
    
    val filteredResults = if (query.isEmpty()) {
        emptyList()
    } else {
        allPlants.filter { plant ->
            plant.name.contains(query, ignoreCase = true) || 
            (plant.scientificName?.contains(query, ignoreCase = true) ?: false) ||
            plant.ethnomedicinal.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F5E9), Color.White)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column {
                Text(
                    text = t.getString("explore_search_title"),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = t.getString("explore_search_subtitle"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF388E3C)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(t.getString("search_placeholder"), color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1B5E20)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20),
                        unfocusedBorderColor = Color(0xFFC8E6C9)
                    )
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            if (query.isEmpty()) {
                // SECCIÓN: ÚLTIMA BÚSQUEDA
                if (viewModel.lastSearchQuery.isNotEmpty()) {
                    Text(t.getString("continue_searching"), fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    SuggestionChip(text = viewModel.lastSearchQuery, onClick = { query = viewModel.lastSearchQuery })
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                PopularSuggestionsSection(t, onSuggestionClick = { 
                    query = it
                    viewModel.saveLastSearch(it)
                })
            } else if (filteredResults.isEmpty()) {
                EmptyState(t)
            } else {
                Text(
                    text = t.getString("plants_found").replace("%d", filteredResults.size.toString()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Guardar la búsqueda exitosa
                LaunchedEffect(query) {
                    if (query.length > 2) viewModel.saveLastSearch(query)
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredResults) { plant ->
                        SearchResultCard(plant, onPlantClick)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(plant: Plant, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(plant.name) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = plant.fullImageUrl,
                contentDescription = plant.name,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = plant.name, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = plant.scientificName ?: "", fontSize = 12.sp, color = Color(0xFF388E3C))
            }
            Icon(Icons.Default.LocalHospital, null, tint = Color(0xFFC8E6C9))
        }
    }
}

@Composable
fun PopularSuggestionsSection(t: TranslationManager, onSuggestionClick: (String) -> Unit) {
    Column {
        Text(t.getString("popular_suggestions"), fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        val suggestions = listOf("Gripe", "Gases", "Insomnio", "Piel", "Estrés")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.forEach { tag ->
                SuggestionChip(text = tag, onClick = { onSuggestionClick(tag) })
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = Color(0xFFE8F5E9),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = Color(0xFF1B5E20),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(t: TranslationManager) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Warning, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
            Text(t.getString("no_results_search"), color = Color.Gray)
        }
    }
}
