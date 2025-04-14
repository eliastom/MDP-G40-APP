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

    NavHost(navController = navController, startDestination = Screen.Loading.route) {

        composable(Screen.Loading.route) {
            LoadingScreen { isLoggedIn ->
                val target = if (isLoggedIn) Screen.Home.route else Screen.Welcome.route
                navController.navigate(target) {
                    popUpTo(Screen.Loading.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLogin = {
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignIn.route) {
            val authViewModel: AuthViewModel = viewModel()
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

        composable(Screen.Home.route) {
            val authViewModel: AuthViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()
            ReflectlyTheme {
                HomeScreen(
                    authViewModel = authViewModel,
                    taskViewModel = taskViewModel,
                    onDateSelected = { date ->
                        navController.navigate(Screen.Journal.withArgs(date))
                    },
                    onLogOut = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onAddTask = {
                        navController.navigate(Screen.NewTask.route)
                    }
                )
            }
        }

        composable(Screen.Journal.route) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: today
            val journalViewModel: JournalViewModel = viewModel()
            ReflectlyTheme {
                JournalScreen(
                    selectedDate = date,
                    viewModel = journalViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.NewTask.route) {
            val taskViewModel: TaskViewModel = viewModel()
            NewTaskScreen(
                viewModel = taskViewModel,
                onNavigation = {
                    navController.popBackStack()
                }
            )
        }
    }
}
