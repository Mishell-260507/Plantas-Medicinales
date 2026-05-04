package com.iiap.plantasmedicinales.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.iiap.plantasmedicinales.ui.detail.PlantDetailScreen
import com.iiap.plantasmedicinales.ui.login.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object Detail : Screen("detail/{plantName}") {
        fun createRoute(plantName: String) = "detail/$plantName"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    val plantViewModel: PlantViewModel = viewModel()
    
    // Verificamos si hay un usuario logueado en Firebase
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startRoute = if (currentUser != null) Screen.Main.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startRoute
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = plantViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = plantViewModel,
                onPlantClick = { plantName ->
                    navController.navigate(Screen.Detail.createRoute(plantName))
                },
                onLogout = {
                    plantViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onProfileClick = {
                    // Acción para ir al perfil
                    // Aquí podrías navegar a una pantalla de perfil si la tienes
                }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("plantName") { type = NavType.StringType })
        ) { backStackEntry ->
            val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
            PlantDetailScreen(
                plantName = plantName,
                viewModel = plantViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
