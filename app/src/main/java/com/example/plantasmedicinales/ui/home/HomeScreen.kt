package com.example.plantasmedicinales.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.plantasmedicinales.data.Plant
import com.example.plantasmedicinales.data.PlantRepository
import com.example.plantasmedicinales.ui.theme.PlantasMedicinalesTheme

@Composable
fun HomeScreen(onPlantClick: (String) -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }
    
    val categories = listOf("Todas", "Fiebre", "Estómago", "Piel", "Energía")
    val allPlants = PlantRepository.allPlants

    val filteredPlants = allPlants.filter { plant ->
        val matchesSearch = plant.name.contains(searchQuery, ignoreCase = true) || 
                          plant.scientificName.contains(searchQuery, ignoreCase = true) ||
                          plant.ailments.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesCategory = selectedCategory == "Todas" || plant.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        HomeHeader()
        Spacer(modifier = Modifier.height(24.dp))
        SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
        Spacer(modifier = Modifier.height(24.dp))
        CategoryList(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategoryClick = { selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (filteredPlants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No se encontraron plantas", color = Color.Gray)
            }
        } else {
            PlantGrid(filteredPlants, onPlantClick)
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Eco, "Logo", tint = Color(0xFF1B5E20), modifier = Modifier.size(32.dp))
        Icon(Icons.Default.AccountCircle, "Profile", tint = Color.Gray, modifier = Modifier.size(40.dp))
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text("El Bosque", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
    Text("Explora la farmacia viviente.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Busca una dolencia o planta...", color = Color.Gray) },
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
}

@Composable
fun CategoryList(categories: List<String>, selectedCategory: String, onCategoryClick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                color = if (isSelected) Color(0xFF00C853) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(20.dp),
                onClick = { onCategoryClick(category) }
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = if (isSelected) Color.White else Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PlantGrid(plants: List<Plant>, onPlantClick: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(plants) { plant ->
            PlantCard(plant, onPlantClick)
        }
    }
}

@Composable
fun PlantCard(plant: Plant, onPlantClick: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().clickable { onPlantClick(plant.name) }
    ) {
        Box(modifier = Modifier.height(if (plant.name.length > 10) 220.dp else 180.dp)) {
            AsyncImage(
                model = plant.imageUrl,
                contentDescription = plant.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Text(text = plant.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = plant.scientificName, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                color = Color.White.copy(alpha = 0.3f),
                shape = CircleShape
            ) {
                Icon(Icons.Default.BookmarkBorder, null, tint = Color.White, modifier = Modifier.padding(6.dp).size(20.dp))
            }
        }
    }
}
