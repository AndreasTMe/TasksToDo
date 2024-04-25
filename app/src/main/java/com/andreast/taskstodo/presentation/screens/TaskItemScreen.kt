package com.andreast.taskstodo.presentation.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.presentation.components.InputDialog
import com.andreast.taskstodo.presentation.components.InputField
import com.andreast.taskstodo.presentation.components.tasks.RecursiveTaskItemRow
import kotlinx.coroutines.launch

@Composable
fun TaskItemScreen(
    taskScreenService: ITaskScreenService,
    navHostController: NavHostController,
    taskListId: Long
) {
    val coroutineScope = rememberCoroutineScope()

    val (taskList, setTaskList) = remember { mutableStateOf(TaskListDto()) }
    val (selectedItem, setSelectedItem) = remember { mutableStateOf<TaskListItemDto?>(null) }
    val (isDialogOpen, setIsDialogOpen) = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            setTaskList(taskScreenService.getTaskListWithItems(taskListId))
        }
    }

    Scaffold(
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
                        InputField(
                            value = taskList.title,
                            placeholder = "Title",
                            onValueChange = {
                                setTaskList(taskList.copy(title = it))
                            },
                            onFocusChange = { state, valueChanged ->
                                if (!state.hasFocus && valueChanged) {
                                    coroutineScope.launch {
                                        taskScreenService.upsertTaskList(taskList)
                                    }
                                }
                            }
                        )
                    }
                    Box {
                        LazyColumn {
                            items(taskList.items.size) { it ->
                                RecursiveTaskItemRow(
                                    task = taskList.items[it],
                                    onCheckTask = { id, isChecked ->
                                        coroutineScope.launch {
                                            taskScreenService.updateTaskListItemCompletedState(
                                                id,
                                                isChecked
                                            )
                                        }
                                    },
                                    onEditTask = { taskToEdit ->
                                        setSelectedItem(taskToEdit)
                                    },
                                    onDeleteTask = { id ->
                                        coroutineScope.launch {
                                            taskScreenService.deleteTaskListItemById(id)
                                        }
                                    },
                                    onAddSubTask = { parent ->
                                        setSelectedItem(parent)
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
                        setIsDialogOpen(true)
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task")
                    Text(
                        modifier = Modifier.weight(1f),
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

    if (isDialogOpen) {
        InputDialog(
            label = "New Task",
            placeholder = "Do the thing...",
            onDismissRequest = {
                setIsDialogOpen(false)
            },
            onConfirmation = {
                if (it == "") {
                    setIsDialogOpen(false)
                    return@InputDialog
                }

                val newTaskListItem = TaskListItemDto(
                    title = it,
                    taskListId = taskListId,
                    order = 0
                )

                coroutineScope.launch {
                    val id =
                        taskScreenService.upsertTaskListItem(newTaskListItem)

                    if (id > 0) {
                        setTaskList(
                            taskList.copy(
                                items = mutableListOf(newTaskListItem)
                            )
                        )
                    }

                    setIsDialogOpen(false)
                }
            },
        )
    }
}