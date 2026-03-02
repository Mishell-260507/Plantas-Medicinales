package com.example.plantasmedicinales.ui.mybotica

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantasmedicinales.R
import com.example.plantasmedicinales.ui.theme.PlantasMedicinalesTheme

data class SavedPlant(val name: String, val tags: List<String>, val imageRes: Int, val bgColor: Color)

@Composable
fun MyBoticaScreen(onPlantClick: (String) -> Unit = {}) {
    val savedPlants = listOf(
        SavedPlant("Uña de Gato", listOf("Inflamación", "Inmune"), R.drawable.ic_launcher_background, Color(0xFFE8F5E9)),
        SavedPlant("Sangre de Grado", listOf("Cicatrizante", "Ulceras"), R.drawable.ic_launcher_background, Color(0xFFFBE9E7)),
        SavedPlant("Matico", listOf("Respiratorio", "Antiséptico"), R.drawable.ic_launcher_background, Color(0xFFE0F2F1)),
        SavedPlant("Achiote", listOf("Próstata", "Antiinflamatorio"), R.drawable.ic_launcher_background, Color(0xFFF9FBE7))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        BoticaHeader(plantCount = savedPlants.size)
        Spacer(modifier = Modifier.height(24.dp))
        SavedPlantList(savedPlants, onPlantClick)
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
        text = "Colección personal de remedios",
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
fun SavedPlantList(plants: List<SavedPlant>, onPlantClick: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(plants) { plant ->
            SavedPlantItem(plant, onPlantClick)
        }
    }
}

@Composable
fun SavedPlantItem(plant: SavedPlant, onPlantClick: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlantClick(plant.name) },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = plant.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(plant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    plant.tags.forEach { tag ->
                        Text(tag, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            Icon(Icons.Default.Bookmark, contentDescription = "Guardado", tint = Color(0xFF00C853))
            Icon(Icons.Default.ChevronRight, contentDescription = "Detalle", tint = Color.LightGray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyBoticaScreenPreview() {
    PlantasMedicinalesTheme {
        MyBoticaScreen(onPlantClick = {})
    }
}
