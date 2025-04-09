package com.g40.reflectly.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.g40.reflectly.ui.screens.*
import com.g40.reflectly.ui.theme.ReflectlyTheme
import com.g40.reflectly.viewmodel.*

import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O) // Requires API 26+ because of LocalDate
@Composable
fun AppNavGraph(navController: NavHostController) {
    val today = LocalDate.now().toString() // Get today's date as a string

    // Define the navigation graph starting at LoadingScreen
    NavHost(navController = navController, startDestination = "LoadingScreen") {

        // Loading screen: decides where to go next based on login status
        composable("LoadingScreen") {
            LoadingScreen { isLoggedIn ->
                val target = if (isLoggedIn) "HomeScreen" else "SignUpScreen"
                navController.navigate(target) {
                    popUpTo("LoadingScreen") { inclusive = true } // Clear backstack
                }
            }
        }

        // Sign-up screen
        composable("SignUpScreen") {
            val authViewModel: AuthViewModel = viewModel() // ViewModel for sign-up logic
            ReflectlyTheme {
                SignUpScreen(
                    viewModel = authViewModel,
                    onNavigate = {
                        navController.navigate("HomeScreen") {
                            popUpTo("SignUpScreen") { inclusive = true } // Clear backstack
                        }
                    }
                )
            }
        }

        // Home screen
        composable("HomeScreen") {
            val homeScreenViewModel: HomeScreenViewModel = viewModel() // ViewModel for home logic
            ReflectlyTheme {
                HomeScreen(viewModel = homeScreenViewModel) {
                    navController.navigate("NewJournalScreen/$today") // Navigate with today's date
                }
            }
        }

        // New Journal screen with date argument
        composable("NewJournalScreen/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: today
            val journalViewModel: NewJournalViewModel = viewModel() // ViewModel for journal screen
            ReflectlyTheme {
                NewJournalScreen(
                    selectedDate = date,
                    viewModel = journalViewModel,
                    onNavigate = { navController.navigate("HomeScreen") } // Back to home
                )
            }
        }
    }
}
