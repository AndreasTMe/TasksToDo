package com.andreast.taskstodo.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.presentation.components.dialogs.InputDialog
import com.andreast.taskstodo.presentation.components.tasks.TaskItemRow
import com.andreast.taskstodo.presentation.components.tasks.TaskListScreenTopHeader
import com.andreast.taskstodo.presentation.theme.outlineDark
import com.andreast.taskstodo.presentation.theme.outlineLight
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    taskListScreenViewModel: TaskListScreenViewModel,
    navHostController: NavHostController
) {
    val isDarkTheme = isSystemInDarkTheme()

    val taskScreenState = taskListScreenViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyListItemInfo = remember { mutableStateOf<LazyListItemInfo?>(null) }
    val draggedDistance = remember { mutableFloatStateOf(0f) }
    val draggedPosition = remember { mutableStateOf(Offset.Zero) }
    val dropItemIndex = remember { mutableIntStateOf(-1) }
    val dragScrollJob = remember { mutableStateOf<Job?>(null) }
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
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDrag = { change, offset ->
                                    change.consume()

                                    if (lazyListItemInfo.value == null) {
                                        return@detectDragGesturesAfterLongPress
                                    }

                                    val draggedItem = lazyListItemInfo.value!!
                                    val draggedOffsetStart = draggedItem.offset
                                    val draggedOffsetEnd = draggedOffsetStart + draggedItem.size

                                    if (draggedOffsetStart == 0 && draggedOffsetEnd == 0) {
                                        return@detectDragGesturesAfterLongPress
                                    }

                                    draggedDistance.floatValue += offset.y

                                    draggedItem
                                        .getDropItemIndex(
                                            lazyListState,
                                            draggedOffsetStart + draggedDistance.floatValue,
                                            draggedOffsetEnd + draggedDistance.floatValue
                                        )
                                        .also { index ->
                                            dropItemIndex.intValue = index
                                        }

                                    draggedItem
                                        .getScrollAmount(
                                            lazyListState,
                                            draggedDistance.floatValue
                                        )
                                        .takeIf {
                                            it != null
                                        }
                                        ?.also { amount ->
                                            dragScrollJob.value = coroutineScope.launch {
                                                lazyListState.scrollBy(amount)
                                            }
                                        } ?: kotlin.run { dragScrollJob.value?.cancel() }
                                },
                                onDragStart = { offset ->
                                    lazyListState
                                        .getDraggedItem(offset)
                                        ?.also {
                                            lazyListItemInfo.value = it
                                        }
                                },
                                onDragEnd = {
                                    lazyListItemInfo.value = null
                                    draggedDistance.floatValue = 0f
                                    draggedPosition.value = Offset.Zero
                                    dropItemIndex.intValue = -1
                                },
                                onDragCancel = {
                                    lazyListItemInfo.value = null
                                    draggedDistance.floatValue = 0f
                                    draggedPosition.value = Offset.Zero
                                    dropItemIndex.intValue = -1
                                }
                            )
                        }
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    state = lazyListState
                ) {
                    itemsIndexed(taskScreenState.value.items) { index, item ->
                        TaskItemRow(
                            modifier = Modifier
                                .onGloballyPositioned {
                                    if (index == lazyListItemInfo.value?.index) {
                                        draggedPosition.value = it.positionInRoot()
                                    }
                                },
                            background = when (index) {
                                lazyListItemInfo.value?.index -> MaterialTheme.colorScheme.surfaceVariant
                                dropItemIndex.intValue -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            },
                            task = item,
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

    if (lazyListItemInfo.value != null) {
        val targetSize = remember { mutableStateOf(IntSize.Zero) }

        val offset = lazyListItemInfo.value?.getDragVerticalOffset(
            lazyListState,
            draggedDistance.floatValue,
            draggedPosition.value.y
        ) ?: 0f

        Box(modifier = Modifier
            .drawBehind {
                drawLine(
                    color = if (isDarkTheme) outlineDark else outlineLight,
                    start = Offset(0f, offset),
                    end = Offset(size.width, offset),
                    strokeWidth = 2.dp.toPx()
                )
            }
        ) {
            TaskItemRow(
                modifier = Modifier
                    .graphicsLayer {
                        alpha = if (targetSize.value == IntSize.Zero) 0f else .8f

                        translationY += offset
                    }
                    .onGloballyPositioned {
                        targetSize.value = it.size
                    },
                task = taskScreenState.value.items[lazyListItemInfo.value!!.index],
            )
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

private fun LazyListState.getDraggedItem(offset: Offset): LazyListItemInfo? {
    return this.layoutInfo.visibleItemsInfo
        .firstOrNull {
            offset.y.toInt() in it.offset..(it.offset + it.size)
        }
}

private fun LazyListItemInfo.getDropItemIndex(
    lazyListState: LazyListState,
    startOffset: Float,
    endOffset: Float
): Int {
    return this.index
        .let {
            lazyListState.layoutInfo.visibleItemsInfo
                .getOrNull(it - lazyListState.layoutInfo.visibleItemsInfo.first().index)
        }
        .let { current ->
            if (current == null) {
                return@let -1
            }

            val found = lazyListState.layoutInfo.visibleItemsInfo
                .filterNot { item ->
                    startOffset > item.offset + item.size
                            || endOffset < item.offset
                            || current.index == item.index
                }
                .firstOrNull { item ->
                    when {
                        startOffset > current.offset -> (endOffset > item.offset + item.size)
                        else -> (startOffset < item.offset)
                    }
                }

            return@let found?.index ?: -1
        }
}

private fun LazyListItemInfo.getScrollAmount(
    lazyListState: LazyListState,
    draggedDistance: Float
): Float? {
    return this
        .let {
            val startOffsetInner = it.offset + draggedDistance
            val endOffsetInner = it.offset + it.size + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffsetInner - lazyListState.layoutInfo.viewportEndOffset)
                    .takeIf { diff ->
                        diff > 0f
                    }

                draggedDistance < 0 -> (startOffsetInner - lazyListState.layoutInfo.viewportStartOffset)
                    .takeIf { diff ->
                        diff < 0f
                    }

                else -> 0f
            }
        }
}

private fun LazyListItemInfo.getDragVerticalOffset(
    lazyListState: LazyListState,
    draggedDistance: Float,
    verticalPosition: Float
): Float {
    return (this.index
        .let {
            lazyListState.layoutInfo.visibleItemsInfo.getOrNull(it - lazyListState.layoutInfo.visibleItemsInfo.first().index)
        }
        ?.let { item ->
            this.offset.toFloat() + draggedDistance - item.offset
        } ?: 0f) + verticalPosition
}