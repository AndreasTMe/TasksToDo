package com.andreast.taskstodo.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.presentation.components.InputDialog
import com.andreast.taskstodo.presentation.components.tasks.TaskListsScreenTopHeader
import kotlinx.coroutines.launch

@Composable
fun TaskListsScreen(
    taskListsScreenViewModel: TaskListsScreenViewModel,
    navHostController: NavHostController
) {
    val taskListsScreenState = taskListsScreenViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val isInputDialogOpen = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                TaskListsScreenTopHeader(title = "All Task Lists")
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    items(taskListsScreenState.value.lists.size) {
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
                                        route = Screen.TaskListScreen.createRoute(taskListId = taskListsScreenState.value.lists[it].id.toString())
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectDragGesturesAfterLongPress(
                                        onDrag = { change, offset ->

                                        },
                                        onDragStart = { offset ->

                                        },
                                        onDragEnd = {

                                        },
                                        onDragCancel = {

                                        }
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
                                    text = taskListsScreenState.value.lists[it].title,
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
                    Icon(Icons.Filled.Add, contentDescription = "Add Task List")
                },
                onClick = {
                    isInputDialogOpen.value = !isInputDialogOpen.value
                }
            )

            if (isInputDialogOpen.value) {
                InputDialog(
                    label = "New List",
                    placeholder = "Enter title...",
                    onConfirmRequest = {
                        if (it == "") {
                            return@InputDialog
                        }

                        coroutineScope.launch {
                            val taskListId =
                                taskListsScreenViewModel.handleTaskListTitleChange(title = it)
                            assert(taskListId > 0) { "Task list creation returned $taskListId. This should never happen!" }

                            navHostController.navigate(
                                route = Screen.TaskListScreen.createRoute(taskListId.toString())
                            )
                        }
                    },
                    onFinally = {
                        isInputDialogOpen.value = false
                    }
                )
            }
        }
    )
}