package com.andreast.taskstodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andreast.taskstodo.screens.Screen
import com.andreast.taskstodo.screens.TASK_ITEM_SCREEN_ROUTE_KEY
import com.andreast.taskstodo.screens.TaskItemScreen
import com.andreast.taskstodo.screens.TaskScreen
import com.andreast.taskstodo.ui.theme.TasksToDoTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasksToDoTheme {
                navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.TaskScreen.route
                ) {
                    composable(
                        route = Screen.TaskScreen.route
                    ) {
                        TaskScreen(navController)
                    }
                    composable(
                        route = Screen.TaskItemScreen.route,
                        arguments = listOf(navArgument(TASK_ITEM_SCREEN_ROUTE_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                        })
                    ) {
                        TaskItemScreen(
                            navController,
                            it.arguments?.getString(TASK_ITEM_SCREEN_ROUTE_KEY).orEmpty()
                        )
                    }
                }
            }
        }
    }
}