package com.example.plantasmedicinales.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.plantasmedicinales.ui.home.HomeScreen
import com.example.plantasmedicinales.ui.mybotica.MyBoticaScreen
import com.example.plantasmedicinales.ui.profile.ProfileScreen
import com.example.plantasmedicinales.ui.search.SearchScreen

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Inicio : BottomBarScreen("inicio", "Inicio", Icons.Filled.Eco, Icons.Outlined.Eco)
    object Buscar : BottomBarScreen("buscar", "Buscar", Icons.Filled.Search, Icons.Outlined.Search)
    object Botica : BottomBarScreen("botica", "Mi Botica", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Perfil : BottomBarScreen("perfil", "Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainScreen(
    viewModel: PlantViewModel,
    onPlantClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val screens = listOf(
        BottomBarScreen.Inicio,
        BottomBarScreen.Buscar,
        BottomBarScreen.Botica,
        BottomBarScreen.Perfil
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title,
                                tint = if (isSelected) Color(0xFF1B5E20) else Color.Gray
                            )
                        },
                        label = { 
                            Text(
                                text = screen.title, 
                                color = if (isSelected) Color(0xFF1B5E20) else Color.Gray 
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomBarScreen.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomBarScreen.Inicio.route) { HomeScreen(onPlantClick = onPlantClick) }
            composable(BottomBarScreen.Buscar.route) { SearchScreen(onPlantClick = onPlantClick) }
            composable(BottomBarScreen.Botica.route) { MyBoticaScreen(viewModel = viewModel, onPlantClick = onPlantClick) }
            composable(BottomBarScreen.Perfil.route) { ProfileScreen(viewModel = viewModel, onLogout = onLogout) }
        }
    }
}
