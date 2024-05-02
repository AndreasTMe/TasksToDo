package com.andreast.taskstodo.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.presentation.components.ConfirmDialog
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
    val dragState = remember { mutableStateOf(DragState<TaskListDto>()) }
    val isInputDialogOpen = remember { mutableStateOf(false) }
    val isConfirmDialogOpen = remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val cellsPerRow = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3

    val itemPadding = 8
    val itemEdge = (screenWidth / cellsPerRow - 2 * itemPadding).toFloat()
    val itemSize = Size(itemEdge, itemEdge)
    val dropItemScale = 1.25f
    val dropItemAlpha = 0.8f

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    columns = GridCells.Fixed(cellsPerRow)
                ) {
                    items(
                        count = taskListsScreenState.value.lists.size,
                        key = { index -> taskListsScreenState.value.lists[index].id }
                    ) { index ->
                        DraggableItem(
                            dropData = taskListsScreenState.value.lists[index],
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
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(itemPadding.dp)
                                    .size(itemSize.width.dp, itemSize.height.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color.Gray,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .background(
                                        color = Color.White,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .align(Alignment.CenterHorizontally)
                                    .clickable {
                                        if (dragState.value.isDragging) {
                                            return@clickable
                                        }

                                        navHostController.navigate(
                                            route = ScreenInfo.TaskListScreen.createRoute(
                                                taskListId = taskListsScreenState.value.lists[index].id.toString()
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = taskListsScreenState.value.lists[index].title
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = dragState.value.isDragging,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                DropArea(
                    dragState = dragState.value
                ) { canDrop, data ->
                    if (data != null) {
                        isConfirmDialogOpen.value = true
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        if (canDrop)
                                            Color.Red.copy(0.25f)
                                        else
                                            Color.DarkGray.copy(0.25f),
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (canDrop) Color.Red else Color.DarkGray)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(36.dp),
                                tint = Color.White,
                                imageVector = Icons.Filled.Delete,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(36.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Task List"
                    )
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
                                route = ScreenInfo.TaskListScreen.createRoute(taskListId.toString())
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

    if (dragState.value.isDragging) {
        val draggableItemSize = remember { mutableStateOf(IntSize.Zero) }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = dropItemScale
                    scaleY = dropItemScale

                    alpha = if (draggableItemSize.value == IntSize.Zero) 0f else dropItemAlpha

                    val offset = dragState.value.position + dragState.value.offset
                    translationX = offset.x.minus(draggableItemSize.value.width / 2)
                    translationY = offset.y.minus(draggableItemSize.value.height / 2)
                }
                .onGloballyPositioned { coordinates ->
                    draggableItemSize.value = coordinates.size
                }
        ) {
            dragState.value.preview.invoke()
        }
    }

    if (isConfirmDialogOpen.value) {
        ConfirmDialog(
            imageVector = Icons.Rounded.Warning,
            label = "Are you sure you want to delete the full list? This action cannot be undone!",
            onConfirmRequest = {
                val selectedId = dragState.value.dropData?.id ?: return@ConfirmDialog

                coroutineScope.launch {
                    taskListsScreenViewModel.handleTaskListDelete(selectedId)
                }
            },
            onFinally = {
                isConfirmDialogOpen.value = false
            }
        )
    }
}

@Preview
@Composable
fun TaskListsScreenPreview() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val cellsPerRow = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2

    val itemPadding = 8
    val itemEdge = (screenWidth / cellsPerRow - 2 * itemPadding).toFloat()
    val itemSize = Size(itemEdge, itemEdge)

    val lists = listOf(
        TaskListDto(1, "Test 1"),
        TaskListDto(2, "Test 2"),
        TaskListDto(3, "Test 3"),
        TaskListDto(4, "Test 4"),
        TaskListDto(5, "Test 5")
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    columns = GridCells.Fixed(2)
                ) {
                    items(
                        count = lists.size,
                        key = { index -> lists[index].id }
                    ) { index ->
                        DraggableItem(
                            dropData = lists[index],
                            onDrag = { state ->
                            },
                            onDragStart = { state ->
                            },
                            onDragEnd = { state ->
                            },
                            onDragCancel = { state ->
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(itemPadding.dp)
                                    .size(itemSize.width.dp, itemSize.height.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color.Gray,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .background(
                                        color = Color.White,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lists[index].title
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                content = {
                    Icon(
                        modifier = Modifier
                            .size(36.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Task List"
                    )
                },
                onClick = { }
            )
        }
    )
}