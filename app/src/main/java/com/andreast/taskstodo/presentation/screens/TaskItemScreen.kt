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
    taskListId: Long
) {
    val coroutineScope = rememberCoroutineScope()
    val (taskList, setTaskList) = remember { mutableStateOf(TaskListDto()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            setTaskList(taskScreenService.getTaskListWithItems(taskListId))
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
                                placeholder = "Do the thing...",
                                onDismissRequest = {
                                    isOpen.value = false
                                },
                                onConfirmation = {
                                    if (it == "") {
                                        isOpen.value = false
                                        return@InputDialog
                                    }

                                    val newTaskListItem = TaskListItemDto(
                                        title = it,
                                        taskListId = taskListId,
                                        order = 1
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

                                        isOpen.value = false
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    })
    BackHandler {
        navHostController.navigate(route = Screen.TaskScreen.route) {
            popUpTo(Screen.TaskScreen.route) {
                inclusive = true
            }
        }
    }
}