package com.g40.reflectly.ui.navigation

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.g40.reflectly.ui.screens.*
import com.g40.reflectly.viewmodel.*
import com.g40.reflectly.ui.theme.ReflectlyTheme
import java.time.LocalDate


@Composable
fun AppNavGraph(navController: NavHostController) {
    val today = LocalDate.now().toString()

    NavHost(navController = navController, startDestination = "loading") {

        composable("loading") {
            LoadingScreen { isLoggedIn ->
                val target = if (isLoggedIn) "home" else "signup"
                navController.navigate(target) {
                    popUpTo("loading") { inclusive = true }
                }
            }
        }

        composable("signup") {
            val authViewModel: AuthViewModel = viewModel()
            ReflectlyTheme {
                SignUpScreen(
                    viewModel = authViewModel,
                    onNavigate = {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("home") {
            val homeScreenViewModel: HomeScreenViewModel = viewModel()
            ReflectlyTheme {
                HomeScreen(
                    viewModel = homeScreenViewModel,
                    onDateSelected = { date ->
                        navController.navigate("journal/$date")
                    }
                )
            }
        }

        composable("journal/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: today
            val journalViewModel: JournalViewModel = viewModel()
            ReflectlyTheme {
                JournalScreen(
                    selectedDate = date,
                    viewModel = journalViewModel,
                    onNavigate = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
