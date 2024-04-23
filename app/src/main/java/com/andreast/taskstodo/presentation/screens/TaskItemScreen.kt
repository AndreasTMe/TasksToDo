package com.andreast.taskstodo.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.presentation.components.ClickableArea
import com.andreast.taskstodo.presentation.components.InputDialog
import com.andreast.taskstodo.presentation.components.InputField
import com.andreast.taskstodo.presentation.components.tasks.RecursiveTaskItemRow
import kotlinx.coroutines.launch

@Composable
fun TaskItemScreen(
    taskScreenService: ITaskScreenService,
    navHostController: NavHostController,
    taskListId: String = ""
) {
    val coroutineScope = rememberCoroutineScope()
    val (taskList, setTaskList) = remember { mutableStateOf(TaskListDto()) }

    val taskListIdParsed = taskListId.toLongOrNull()
    if (taskListIdParsed != null) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                setTaskList(taskScreenService.getTaskListWithItems(taskListIdParsed))
            }
        }
    }

    Scaffold(content = { padding ->
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
                        }
                    )
                }
                Box {
                    LazyColumn {
                        items(taskList.items.size) { it ->
                            RecursiveTaskItemRow(
                                task = taskList.items[it],
                                index = it,
                                onItemChecked = { indexTree ->
                                    val newTaskList = taskList.copy()
                                    if (newTaskList.tryUpdateChecked(indexTree)) {
                                        setTaskList(newTaskList)
                                    }
                                }
                            )
                        }
                    }

                    if (taskList.items.isEmpty()) {
                        val isOpen = remember { mutableStateOf(false) }

                        ClickableArea(onAreaClicked = { isOpen.value = true })

                        if (isOpen.value) {
                            InputDialog(
                                label = "New Task",
                                placeholder = "Task to do...",
                                onDismissRequest = {
                                    isOpen.value = false
                                },
                                onConfirmation = {
                                    if (it != "") {
                                        setTaskList(
                                            taskList.copy(
                                                items = mutableListOf(
                                                    TaskListItemDto(
                                                        title = it,
                                                        order = 1
                                                    )
                                                )
                                            )
                                        )
                                    }

                                    isOpen.value = false
                                },
                            )
                        }
                    }
                }
            }
        }
    })
    BackHandler {
        if (taskList.id.toInt() == 0 && taskList.title != "") {
            coroutineScope.launch {
                taskScreenService.insertTaskList(taskList)
            }
        }

        navHostController.navigate(route = Screen.TaskScreen.route) {
            popUpTo(Screen.TaskScreen.route) {
                inclusive = true
            }
        }
    }
}