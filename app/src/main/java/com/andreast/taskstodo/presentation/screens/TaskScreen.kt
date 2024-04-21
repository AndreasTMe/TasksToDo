package com.andreast.taskstodo.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.services.ITaskScreenService
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(
    taskScreenService: ITaskScreenService,
    navHostController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    val (taskLists, setTaskLists) = remember { mutableStateOf<List<TaskListDto>>(listOf()) }

    Scaffold(
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    coroutineScope.launch {
                        setTaskLists(taskScreenService.getAllTaskLists())
                    }

                    items(taskLists.size) {
                        Column(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(8.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.Gray,
                                    shape = MaterialTheme.shapes.small
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable {
                                    navHostController.navigate(
                                        route = Screen.TaskItemScreen.createRoute(taskId = taskLists[it].id.toString())
                                    )
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = taskLists[it].title ?: "Untitled",
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                },
                onClick = {
                    navHostController.navigate(
                        route = Screen.TaskItemScreen.createRoute()
                    )
                }
            )
        }
    )
}