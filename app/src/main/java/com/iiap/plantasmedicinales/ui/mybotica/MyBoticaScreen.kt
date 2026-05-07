package com.iiap.plantasmedicinales.ui.mybotica

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iiap.plantasmedicinales.data.Plant
import com.iiap.plantasmedicinales.ui.PlantViewModel

@Composable
fun MyBoticaScreen(viewModel: PlantViewModel, onPlantClick: (String) -> Unit = {}) {
    val savedPlants = viewModel.getSavedPlants()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        BoticaHeader(plantCount = savedPlants.size)
        Spacer(modifier = Modifier.height(24.dp))
        
        if (savedPlants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Aún no tienes plantas guardadas", color = Color.Gray)
                }
            }
        } else {
            SavedPlantList(savedPlants, onPlantClick)
        }
    }
}

@Composable
fun BoticaHeader(plantCount: Int) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Mi Botica",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1B5E20)
    )
    Text(
        text = "Tu colección personal de medicina natural",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(16.dp))
    BoticaChip(count = plantCount)
}

@Composable
fun BoticaChip(count: Int) {
    Surface(
        color = Color(0xFFE8F5E9),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(Color(0xFF00C853), RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$count PLANTAS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF1B5E20)
            )
        }
    }
}

@Composable
fun SavedPlantList(plants: List<Plant>, onPlantClick: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(plants) { plant ->
            SavedPlantItem(plant, onPlantClick)
        }
    }
}

@Composable
fun SavedPlantItem(plant: Plant, onPlantClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlantClick(plant.name) },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = plant.fullImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(plant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = plant.scientificName ?: "",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Icon(Icons.Default.Bookmark, contentDescription = "Guardado", tint = Color(0xFF00C853))
            Icon(Icons.Default.ChevronRight, contentDescription = "Detalle", tint = Color.LightGray)
        }
    }
}
