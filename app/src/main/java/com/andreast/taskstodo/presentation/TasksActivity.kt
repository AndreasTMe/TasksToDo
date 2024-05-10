package com.andreast.taskstodo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.andreast.taskstodo.presentation.screens.ScreenInfo
import com.andreast.taskstodo.presentation.screens.TaskListScreen
import com.andreast.taskstodo.presentation.screens.TaskListScreenViewModel
import com.andreast.taskstodo.presentation.screens.TaskListsScreen
import com.andreast.taskstodo.presentation.screens.TaskListsScreenViewModel
import com.andreast.taskstodo.presentation.theme.TasksToDoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksActivity : ComponentActivity() {

    private lateinit var _navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasksToDoTheme {
                _navController = rememberNavController()

                NavHost(
                    navController = _navController,
                    startDestination = ScreenInfo.TaskListsScreen
                ) {
                    composable<ScreenInfo.TaskListsScreen> {
                        TaskListsScreen(hiltViewModel<TaskListsScreenViewModel>(), _navController)
                    }
                    composable<ScreenInfo.TaskListScreen> { navBackStackEntry ->
                        val taskListScreen = navBackStackEntry.toRoute<ScreenInfo.TaskListScreen>()
                        TaskListScreen(
                            hiltViewModel<TaskListScreenViewModel, TaskListScreenViewModel.Factory>(
                                creationCallback = { factory ->
                                    factory.create(taskListId = taskListScreen.id)
                                }
                            ),
                            _navController
                        )
                    }
                }
            }
        }
    }
}