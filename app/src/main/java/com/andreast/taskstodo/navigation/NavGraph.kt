package com.andreast.taskstodo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.andreast.taskstodo.screens.Screen
import com.andreast.taskstodo.screens.TASK_ITEM_SCREEN_ROUTE_KEY
import com.andreast.taskstodo.screens.TaskItemScreen
import com.andreast.taskstodo.screens.TaskScreen

@Composable
fun SetupNavGraph(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.TaskScreen.route
    ) {
        composable(
            route = Screen.TaskScreen.route
        ) {
            TaskScreen(navHostController)
        }
        composable(
            route = Screen.TaskItemScreen.route,
            arguments = listOf(navArgument(TASK_ITEM_SCREEN_ROUTE_KEY) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            TaskItemScreen(
                navHostController,
                it.arguments?.getString(TASK_ITEM_SCREEN_ROUTE_KEY).orEmpty()
            )
        }
    }
}