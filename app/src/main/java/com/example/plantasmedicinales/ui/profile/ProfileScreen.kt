package com.example.plantasmedicinales.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantasmedicinales.ui.PlantViewModel

@Composable
fun ProfileScreen(viewModel: PlantViewModel, onLogout: () -> Unit = {}) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Perfil Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF1B5E20)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // DATOS REALES DEL VIEWMODEL
        Text(
            text = viewModel.userName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )
        Text(
            text = viewModel.userEmail.ifEmpty { "Correo no configurado" },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Opciones con funcionalidad
        ProfileOption(
            icon = Icons.Default.Edit, 
            title = "Mis Datos",
            onClick = { showEditDialog = true }
        )
        ProfileOption(
            icon = Icons.Default.Notifications, 
            title = "Notificaciones",
            onClick = { showNotificationsDialog = true }
        )
        ProfileOption(
            icon = Icons.Default.Security, 
            title = "Privacidad y Seguridad",
            onClick = { showSecurityDialog = true }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Cerrar Sesión
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .clickable { onLogout() },
            color = Color(0xFFFBE9E7),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFD84315)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Cerrar Sesión",
                    color = Color(0xFFD84315),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // DIÁLOGO: MIS DATOS
    if (showEditDialog) {
        var newName by remember { mutableStateOf(viewModel.userName) }
        var newEmail by remember { mutableStateOf(viewModel.userEmail) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Mis Datos", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveUser(newName, newEmail)
                    showEditDialog = false
                }) {
                    Text("Guardar", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // DIÁLOGO: NOTIFICACIONES
    if (showNotificationsDialog) {
        var notificationsEnabled by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Ajustes de Notificaciones", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("Recibir recordatorios de salud", modifier = Modifier.weight(1f))
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF1B5E20))
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Aceptar", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // DIÁLOGO: PRIVACIDAD Y SEGURIDAD
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = { Text("Privacidad y Seguridad", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Tu información está protegida localmente en este dispositivo.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    ListItem(
                        headlineContent = { Text("Cifrado de datos", fontSize = 14.sp) },
                        supportingContent = { Text("Tus favoritos están encriptados.", fontSize = 12.sp) },
                        leadingContent = { Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF1B5E20)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Entendido", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ProfileOption(icon: ImageVector, title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1B5E20),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
