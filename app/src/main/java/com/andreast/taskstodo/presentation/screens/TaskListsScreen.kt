package com.andreast.taskstodo.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.presentation.components.InputDialog
import com.andreast.taskstodo.presentation.components.draganddrop.DragState
import com.andreast.taskstodo.presentation.components.draganddrop.DraggableItem
import com.andreast.taskstodo.presentation.components.draganddrop.DropArea
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
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding)
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .weight(1f),
                    columns = GridCells.Fixed(3)
                ) {
                    items(taskListsScreenState.value.lists.size) { index ->
                        DraggableItem(
                            dropData = taskListsScreenState.value.lists[index],
                            onDrag = {

                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(8.dp)
                                    .fillMaxSize()
                                    .border(
                                        width = 2.dp,
                                        color = Color.Gray,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .align(Alignment.CenterHorizontally)
                                    .clickable {
//                                        if (taskListsScreenState.value.isDragging) {
//                                            return@clickable
//                                        }

                                        navHostController.navigate(
                                            route = Screen.TaskListScreen.createRoute(
                                                taskListId = taskListsScreenState.value.lists[index].id.toString()
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = taskListsScreenState.value.lists[index].title,
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
//                visible = taskListsScreenState.value.isDragging,
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                DropArea<TaskListDto>(
                    dragState = DragState()
                ) { data ->
                    if (data != null) {
                        // TODO: Delete task list
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0.1f, 0.1f, 0.1f, 0.25f),
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    Color.DarkGray,
                                    CircleShape
                                )
                                .padding(4.dp),
                            tint = Color.DarkGray,
                            imageVector = Icons.Filled.Delete,
                            contentDescription = ""
                        )
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