package com.andreast.taskstodo.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.presentation.components.dialogs.InputDialog
import com.andreast.taskstodo.presentation.components.draganddrop.DragState
import com.andreast.taskstodo.presentation.components.tasks.RecursiveTaskRow
import com.andreast.taskstodo.presentation.components.tasks.TaskListScreenTopHeader
import com.andreast.taskstodo.presentation.theme.outlineLight
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    taskListScreenViewModel: TaskListScreenViewModel,
    navHostController: NavHostController
) {
    val taskScreenState = taskListScreenViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val dragState = remember { mutableStateOf(DragState<TaskListItemDto>()) }
    val isDialogOpen = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .height(40.dp),
                    onClick = {
                        navHostController.navigate(route = ScreenInfo.TaskListsScreen.route) {
                            popUpTo(ScreenInfo.TaskListsScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "'${taskScreenState.value.list.title}' Back Button"
                    )
                }
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                TaskListScreenTopHeader(
                    title = taskScreenState.value.list.title,

                    onUncheckCompleted = {
                        coroutineScope.launch {
                            taskListScreenViewModel.handleTaskListUncheckCompleted()
                        }
                    },
                    onRemoveCompleted = {
                        coroutineScope.launch {
                            taskListScreenViewModel.handleTaskListRemoveCompleted()
                        }
                    },
                    onEditTitle = {
                        coroutineScope.launch {
                            taskListScreenViewModel.handleTaskListTitleChange(it)
                        }
                    },
                    onDeleteList = {
                        coroutineScope.launch {
                            navHostController.navigate(route = ScreenInfo.TaskListsScreen.route) {
                                popUpTo(ScreenInfo.TaskListsScreen.route) {
                                    inclusive = true
                                }
                            }
                            taskListScreenViewModel.handleTaskListDelete()
                        }
                    }
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                ) {
                    items(taskScreenState.value.items.size) { it ->
                        RecursiveTaskRow(
                            task = taskScreenState.value.items[it],
                            onDrag = { state ->
                                dragState.value = state
                            },
                            onDragStart = { state ->
                                dragState.value = state
                            },
                            onDragEnd = { state ->
                                dragState.value = state
                            },
                            onDragCancel = { state ->
                                dragState.value = state
                            },
                            onCheckTask = { id, isChecked ->
                                if (dragState.value.isDragging) {
                                    return@RecursiveTaskRow
                                }

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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        taskListScreenViewModel.selectItem(null, TaskListScreenAction.AddTask)
                        isDialogOpen.value = true
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(28.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task"
                    )
                    Text(
                        text = "Add task",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    )

    BackHandler {
        navHostController.navigate(route = ScreenInfo.TaskListsScreen.route) {
            popUpTo(ScreenInfo.TaskListsScreen.route) {
                inclusive = true
            }
        }
    }

    if (isDialogOpen.value) {
        InputDialog(
            header = getDialogLabel(
                taskScreenState.value.screenAction,
                taskScreenState.value.selectedItem != null
            ),
            label = "Title",
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


    if (dragState.value.isDragging) {
        val targetSize = remember { mutableStateOf(IntSize.Zero) }

        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    translationY = (dragState.value.position.y + dragState.value.offset.y)
                        .minus(targetSize.value.height / 2)

                    alpha = if (targetSize.value == IntSize.Zero) 0f else 0.8f
                }
                .onGloballyPositioned {
                    targetSize.value = it.size
                }
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .drawBehind {
                        drawLine(
                            outlineLight,
                            Offset.Zero,
                            Offset(size.width, 0f),
                            2 * density
                        )
                    }
            ) {
                dragState.value.preview.invoke()
            }
        }
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