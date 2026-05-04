package com.iiap.plantasmedicinales.ui.home

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iiap.plantasmedicinales.data.Plant
import com.iiap.plantasmedicinales.data.PlantRepository
import com.iiap.plantasmedicinales.ui.PlantViewModel
import com.iiap.plantasmedicinales.ui.theme.PlantasMedicinalesTheme

@Composable
fun HomeScreen(viewModel: PlantViewModel, onPlantClick: (String) -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }
    
    val categories = listOf("Todas", "Fiebre", "Estómago", "Piel", "Energía")
    val allPlants = viewModel.allPlants

    val filteredPlants = allPlants.filter { plant ->
        val matchesSearch = plant.name.contains(searchQuery, ignoreCase = true) || 
                          plant.scientificName.contains(searchQuery, ignoreCase = true) ||
                          plant.ailments.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesCategory = viewModel.selectedCategory == "Todas" || plant.category == viewModel.selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        HomeHeader(userName = viewModel.userName)
        Spacer(modifier = Modifier.height(24.dp))
        
        // BARRA DE BÚSQUEDA CON ALTO CONTRASTE
        SearchBar(
            query = searchQuery, 
            onQueryChange = { searchQuery = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        CategoryList(
            categories = categories,
            selectedCategory = viewModel.selectedCategory,
            onCategoryClick = { viewModel.selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (filteredPlants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No se encontraron plantas", color = Color.DarkGray)
            }
        } else {
            PlantGrid(filteredPlants, onPlantClick, viewModel)
        }
    }
}

@Composable
fun HomeHeader(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = "Logo",
            tint = Color(0xFF1B5E20),
            modifier = Modifier.size(32.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text("Hola, $userName", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
    Text("Explora la farmacia viviente.", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Busca una dolencia o planta...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1B5E20)) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color(0xFFC8E6C9),
            focusedBorderColor = Color(0xFF1B5E20),
            focusedTextColor = Color.Black, // Texto negro al escribir
            unfocusedTextColor = Color.Black
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
                    color = if (isSelected) Color.White else Color(0xFF1B5E20), // Texto oscuro si no está seleccionado
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlantGrid(plants: List<Plant>, onPlantClick: (String) -> Unit, viewModel: PlantViewModel) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(plants) { plant ->
            PlantCard(plant, onPlantClick, viewModel)
        }
    }
}

@Composable
fun PlantCard(plant: Plant, onPlantClick: (String) -> Unit, viewModel: PlantViewModel) {
    val isSaved = viewModel.isPlantSaved(plant.name)

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
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable { viewModel.toggleSavePlant(plant.name) },
                color = if (isSaved) Color(0xFF00C853) else Color.White.copy(alpha = 0.3f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Guardado" else "No guardado",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp).size(20.dp)
                )
            }
        }
    }
}
