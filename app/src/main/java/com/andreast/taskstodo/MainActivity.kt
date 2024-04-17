package com.andreast.taskstodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.andreast.taskstodo.navigation.SetupNavGraph
import com.andreast.taskstodo.ui.theme.TasksToDoTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasksToDoTheme {
                navController = rememberNavController()
                SetupNavGraph(navHostController = navController)
            }
        }
    }
}