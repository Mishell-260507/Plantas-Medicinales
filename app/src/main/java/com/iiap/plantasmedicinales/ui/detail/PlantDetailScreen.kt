package com.iiap.plantasmedicinales.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iiap.plantasmedicinales.ui.PlantViewModel
import com.iiap.plantasmedicinales.util.TranslationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(plantName: String, viewModel: PlantViewModel, onBack: () -> Unit) {
    val plant = viewModel.allPlants.find { it.name == plantName } ?: return
    val isSaved = viewModel.isPlantSaved(plantName)
    val t = TranslationManager

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t.getString("detail_title"), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t.getString("back_button"))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSavePlant(plantName) }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = t.getString("save_button"),
                            tint = if (isSaved) Color(0xFF00C853) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = plant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Estructura de Tabla tipo IIAP
                InfoRow(t.getString("label_code"), plant.code)
                InfoRow(t.getString("label_common_name"), plant.name)
                InfoRow(t.getString("label_synonyms"), plant.commonSynonyms)
                InfoRow(t.getString("label_scientific_name"), plant.scientificName)
                InfoRow(t.getString("label_family"), plant.family)
                InfoRow(t.getString("label_description"), plant.description)
                InfoRow(t.getString("label_habitat"), plant.habitat)
                InfoRow(t.getString("label_distribution"), plant.distribution)
                InfoRow(t.getString("label_chemical_composition"), plant.chemicalComposition)
                InfoRow(t.getString("label_toxicity"), plant.toxicity)
                InfoRow(t.getString("label_ethnomedicinal"), plant.ethnomedicinal)
                InfoRow(t.getString("label_preparation"), plant.preparation)
                InfoRow(t.getString("label_adverse_effects"), plant.adverseEffects)
                InfoRow(t.getString("label_other_uses"), plant.otherUses)
                InfoRow(t.getString("label_voucher"), plant.voucher)
                InfoRow(t.getString("label_bibliography"), plant.bibliography)
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    if (value.isNotBlank() && value != "null") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(0.4f),
                color = Color(0xFF388E3C), // Verde IIAP
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = value,
                modifier = Modifier.weight(0.6f),
                color = Color.DarkGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
}
