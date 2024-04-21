package com.andreast.taskstodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.presentation.screens.Screen
import com.andreast.taskstodo.presentation.screens.TASK_ITEM_SCREEN_ROUTE_KEY
import com.andreast.taskstodo.presentation.screens.TaskItemScreen
import com.andreast.taskstodo.presentation.screens.TaskScreen
import com.andreast.taskstodo.presentation.ui.theme.TasksToDoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var _navController: NavHostController

    @Inject
    lateinit var taskScreenService: ITaskScreenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TasksToDoTheme {
                _navController = rememberNavController()

                NavHost(
                    navController = _navController,
                    startDestination = Screen.TaskScreen.route
                ) {
                    composable(
                        route = Screen.TaskScreen.route
                    ) {
                        TaskScreen(taskScreenService, _navController)
                    }
                    composable(
                        route = Screen.TaskItemScreen.route,
                        arguments = listOf(navArgument(TASK_ITEM_SCREEN_ROUTE_KEY) {
                            type = NavType.StringType
                            defaultValue = ""
                        })
                    ) {
                        TaskItemScreen(
                            _navController,
                            it.arguments?.getString(TASK_ITEM_SCREEN_ROUTE_KEY).orEmpty()
                        )
                    }
                }
            }
        }
    }
}