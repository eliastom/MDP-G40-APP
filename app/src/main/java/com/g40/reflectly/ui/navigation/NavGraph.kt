package com.g40.reflectly.ui.navigation

import HomeScreen
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.g40.reflectly.data.models.Task
import com.g40.reflectly.ui.screens.*
import com.g40.reflectly.viewmodel.*
import com.g40.reflectly.ui.theme.ReflectlyTheme
import java.time.LocalDate

@Composable
fun AppNavGraph(navController: NavHostController) {
    val today = LocalDate.now().toString()

    // Main Navigation host
    NavHost(navController = navController, startDestination = Screen.Loading.route) {

        // --- Loading Screen ---
        composable(Screen.Loading.route) {
            ReflectlyTheme {
                LoadingScreen { isLoggedIn ->
                    val target = if (isLoggedIn) Screen.Home.route else Screen.Welcome.route
                    navController.navigate(target) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                }
            }
        }

        // --- Welcome Screen ---
        composable(Screen.Welcome.route) {
            ReflectlyTheme {
                WelcomeScreen(
                    onLogin = {
                        navController.navigate(Screen.SignIn.route)
                    },
                    onSignUp = {
                        navController.navigate(Screen.SignUp.route)
                    }
                )
            }
        }

        // --- Sign In Screen ---
        composable(Screen.SignIn.route) {
            val authViewModel: AuthViewModel = viewModel()
            ReflectlyTheme {
                LogInScreen(
                    viewModel = authViewModel,
                    onLogIn = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // --- Sign Up Screen ---
        composable(Screen.SignUp.route) {
            val authViewModel: AuthViewModel = viewModel()
            ReflectlyTheme {
                SignUpScreen(
                    viewModel = authViewModel,
                    onSignUp = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // --- Home Screen ---
        composable(Screen.Home.route) {
            val authViewModel: AuthViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()
            val homeViewModel: HomeViewModel = viewModel()
            val journalViewModel: JournalViewModel = viewModel()
            val gamificationViewModel: GamificationViewModel = viewModel()
            ReflectlyTheme {
                HomeScreen(
                    authViewModel = authViewModel,
                    taskViewModel = taskViewModel,
                    homeViewModel = homeViewModel,
                    gamificationViewModel = gamificationViewModel,
                    journalViewModel = journalViewModel,
                    onDateSelected = { date ->
                        navController.navigate(Screen.Journal.withArgs(date))
                    },
                    onLogOut = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onTaskClick = { task ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("task", task)
                        navController.navigate(Screen.EditTask.route)
                    },
                    onAddTask = {
                        navController.navigate(Screen.NewTask.route)
                    }
                )
            }
        }

        // --- Journal Screen (with date argument) ---
        composable(
            route = Screen.Journal.route,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: today
            val journalViewModel: JournalViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()
            val gamificationViewModel: GamificationViewModel = viewModel()
            ReflectlyTheme {
                JournalScreen(
                    selectedDate = date,
                    viewModel = journalViewModel,
                    taskViewModel = taskViewModel,
                    onBack = { navController.popBackStack() },
                    onTaskClick = { task ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("task", task)
                        navController.navigate(Screen.EditTask.route)
                    },
                    onAddTask = {
                        navController.navigate(Screen.NewTask.route)
                    },
                    gamificationViewModel = gamificationViewModel
                )
            }
        }

        // --- New Task Screen ---
        composable(Screen.NewTask.route) {
            val newTaskViewModel: NewTaskViewModel = viewModel()
            val gamificationViewModel: GamificationViewModel = viewModel()
            ReflectlyTheme {
                NewTaskScreen(
                    viewModel = newTaskViewModel,
                    onNavigation = { navController.popBackStack() },
                    gamificationViewModel = gamificationViewModel
                )
            }
        }

        // --- Edit Task Screen ---
        composable(Screen.EditTask.route) {
            val task = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Task>("task")

            if (task != null) {
                val editTaskViewModel = remember { EditTaskViewModel(task) }

                ReflectlyTheme {
                    EditTaskScreen(
                        viewModel = editTaskViewModel,
                        onNavigation = { navController.popBackStack() }
                    )
                }
            } else {
                // If no task was passed, show fallback UI
                Text("Error: Task not found")
            }
        }
    }
}
