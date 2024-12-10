package com.rcc.tinytasks.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rcc.tinytasks.screens.addtask.AddTaskScreen
import com.rcc.tinytasks.screens.completedtasks.CompletedTasksScreen
import com.rcc.tinytasks.screens.edittask.EditTaskScreen
import com.rcc.tinytasks.screens.splash.SplashScreen
import com.rcc.tinytasks.screens.login.LoginScreen
import com.rcc.tinytasks.screens.home.HomeScreen
import com.rcc.tinytasks.screens.settings.SettingsScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {

        composable(Screens.SplashScreen.name){
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name){
            LoginScreen(navController = navController)
        }
        composable(Screens.HomeScreen.name){
            HomeScreen(navController = navController)
        }
        composable(Screens.AddTaskScreen.name) {
            AddTaskScreen(navController = navController)
        }
        composable("${Screens.EditTaskScreen.name}/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            EditTaskScreen(navController = navController, taskId = taskId)
        }
        composable(Screens.SettingsScreen.name) {
            SettingsScreen(navController = navController)
        }
        composable(Screens.CompletedTasks.name) {
            CompletedTasksScreen(navController = navController)
        }
    }
}