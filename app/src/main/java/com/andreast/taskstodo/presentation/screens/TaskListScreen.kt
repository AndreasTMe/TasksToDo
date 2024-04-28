package com.andreast.taskstodo.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.presentation.components.InputDialog
import com.andreast.taskstodo.presentation.components.tasks.RecursiveTaskItemRow
import com.andreast.taskstodo.presentation.components.tasks.TaskItemScreenTopBar
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    taskListScreenViewModel: TaskListScreenViewModel,
    navHostController: NavHostController
) {
    val taskScreenState = taskListScreenViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val isDialogOpen = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                TaskItemScreenTopBar(
                    title = taskScreenState.value.list.title,
                    onUncheckCompleted = {

                    },
                    onRemoveCompleted = {

                    },
                    onEditTitle = {
                        coroutineScope.launch {
                            taskListScreenViewModel.handleTaskListTitleChange(it)
                        }
                    },
                    onDeleteList = {

                    }
                )
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Box {
                        LazyColumn {
                            items(taskScreenState.value.items.size) { it ->
                                RecursiveTaskItemRow(
                                    task = taskScreenState.value.items[it],
                                    onCheckTask = { id, isChecked ->
                                        coroutineScope.launch {
                                            taskListScreenViewModel.handleTaskListItemCompletedState(
                                                id,
                                                isChecked
                                            )
                                        }
                                    },
                                    onEditTask = { taskToEdit ->
                                        taskListScreenViewModel.selectItem(
                                            taskToEdit,
                                            TaskListScreenAction.EditTask
                                        )
                                        isDialogOpen.value = true
                                    },
                                    onDeleteTask = { id ->
                                        coroutineScope.launch {
                                            taskListScreenViewModel.handleTaskListItemDelete(id)
                                        }
                                    },
                                    onAddSubTask = { parent ->
                                        taskListScreenViewModel.selectItem(
                                            parent,
                                            TaskListScreenAction.AddTask
                                        )

                                        isDialogOpen.value = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        taskListScreenViewModel.selectItem(null, TaskListScreenAction.AddTask)
                        isDialogOpen.value = true
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task")
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Add task"
                    )
                }
            }
        }
    )

    BackHandler {
        navHostController.navigate(route = Screen.TaskScreen.route) {
            popUpTo(Screen.TaskScreen.route) {
                inclusive = true
            }
        }
    }

    if (isDialogOpen.value) {
        InputDialog(
            label = getDialogLabel(
                taskScreenState.value.screenAction,
                taskScreenState.value.selectedItem != null
            ),
            value = getDialogValue(
                taskScreenState.value.screenAction,
                taskScreenState.value.selectedItem
            ),
            placeholder = "Do the thing...",
            onConfirmRequest = {
                coroutineScope.launch {
                    taskListScreenViewModel.handleTaskListScreenAction(it)
                }
            },
            onFinally = {
                isDialogOpen.value = false
            }
        )
    }
}

private fun getDialogLabel(
    taskListScreenAction: TaskListScreenAction,
    hasSelected: Boolean
): String {
    return when (taskListScreenAction) {
        TaskListScreenAction.None -> ""
        TaskListScreenAction.AddTask -> if (hasSelected) "New Sub-task" else "New Task"
        TaskListScreenAction.EditTask -> "Edit Task"
    }
}

private fun getDialogValue(
    taskListScreenAction: TaskListScreenAction,
    selectedItem: TaskListItemDto?
): String {
    return when (taskListScreenAction) {
        TaskListScreenAction.None -> ""
        TaskListScreenAction.AddTask -> ""
        TaskListScreenAction.EditTask -> selectedItem?.title ?: ""
    }
}