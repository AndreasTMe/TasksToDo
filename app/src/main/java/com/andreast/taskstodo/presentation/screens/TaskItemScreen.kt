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

private enum class TaskItemAction {
    None,
    AddTask,
    EditTask
}

@Composable
fun TaskItemScreen(
    taskScreenService: ITaskScreenService,
    navHostController: NavHostController,
    taskListId: Long
) {
    val coroutineScope = rememberCoroutineScope()

    val taskList = remember { mutableStateOf(TaskListDto()) }
    val taskItemAction = remember { mutableStateOf(TaskItemAction.None) }
    val selectedItem = remember { mutableStateOf<TaskListItemDto?>(null) }
    val isDialogOpen = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            taskList.value = taskScreenService.getTaskListWithItems(taskListId)
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
                            value = taskList.value.title,
                            placeholder = "Title",
                            onValueChange = {
                                taskList.value.title = it
                            },
                            onFocusChange = { state, valueChanged ->
                                if (!state.hasFocus && valueChanged) {
                                    coroutineScope.launch {
                                        taskScreenService.upsertTaskList(taskList.value)
                                    }
                                }
                            }
                        )
                    }
                    Box {
                        LazyColumn {
                            items(taskList.value.items.size) { it ->
                                RecursiveTaskItemRow(
                                    task = taskList.value.items[it],
                                    onCheckTask = { id, isChecked ->
                                        coroutineScope.launch {
                                            taskScreenService.updateTaskListItemCompletedState(
                                                id,
                                                isChecked
                                            )
                                        }
                                    },
                                    onEditTask = { taskToEdit ->
                                        taskItemAction.value = TaskItemAction.EditTask
                                        selectedItem.value = taskToEdit
                                        isDialogOpen.value = true
                                    },
                                    onDeleteTask = { taskToDelete ->
                                        coroutineScope.launch {
                                            taskScreenService.deleteTaskListItemById(
                                                taskToDelete.id,
                                                taskList.value
                                            )
                                            taskList.value.removeItem(taskToDelete)
                                        }
                                    },
                                    onAddSubTask = { parent ->
                                        taskItemAction.value = TaskItemAction.AddTask
                                        selectedItem.value = parent
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
                        taskItemAction.value = TaskItemAction.AddTask
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
            label = getDialogLabel(taskItemAction.value, selectedItem.value != null),
            value = getDialogValue(taskItemAction.value, selectedItem.value),
            placeholder = "Do the thing...",
            onConfirmRequest = {
                if (it == "") {
                    return@InputDialog
                }

                val taskListItemToHandle =
                    if (taskItemAction.value == TaskItemAction.EditTask && selectedItem.value != null)
                        selectedItem.value!!
                    else
                        TaskListItemDto(
                            parentId = selectedItem.value?.id,
                            taskListId = taskListId,
                            order = taskList.value.calculateOrder(selectedItem.value?.parentId)
                        )
                taskListItemToHandle.title = it

                coroutineScope.launch {
                    val id =
                        taskScreenService.upsertTaskListItem(taskListItemToHandle)

                    if (taskItemAction.value != TaskItemAction.EditTask && id > 0) {
                        taskListItemToHandle.id = id
                        taskList.value.addItem(taskListItemToHandle)
                    }
                }
            },
            onFinally = {
                taskItemAction.value = TaskItemAction.None
                selectedItem.value = null
                isDialogOpen.value = false
            }
        )
    }
}

private fun getDialogLabel(taskItemAction: TaskItemAction, hasSelected: Boolean): String {
    return when (taskItemAction) {
        TaskItemAction.None -> ""
        TaskItemAction.AddTask -> if (hasSelected) "New Sub-task" else "New Task"
        TaskItemAction.EditTask -> "Edit Task"
    }
}

private fun getDialogValue(taskItemAction: TaskItemAction, selectedItem: TaskListItemDto?): String {
    return when (taskItemAction) {
        TaskItemAction.None -> ""
        TaskItemAction.AddTask -> ""
        TaskItemAction.EditTask -> selectedItem?.title ?: ""
    }
}