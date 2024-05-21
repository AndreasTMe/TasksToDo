package com.andreast.taskstodo.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.presentation.components.InputField
import com.andreast.taskstodo.presentation.components.dialogs.ConfirmDialog
import com.andreast.taskstodo.presentation.components.dialogs.InputDialog
import com.andreast.taskstodo.presentation.components.tasks.TaskListsScreenTopHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListsScreen(
    taskListsScreenViewModel: TaskListsScreenViewModel,
    navHostController: NavHostController
) {
    val taskListsScreenState = taskListsScreenViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val search = remember { mutableStateOf("") }
    val selectedList = remember { mutableStateOf<TaskListDto?>(null) }
    val isInputDialogOpen = remember { mutableStateOf(false) }
    val isConfirmDialogOpen = remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current

    val itemsPerRow = remember {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
    }
    val itemPadding = remember { 8 }
    val itemEdge = remember {
        (configuration.screenWidthDp / itemsPerRow - 2 * itemPadding).toFloat()
    }
    val itemSize = remember { Size(itemEdge, itemEdge) }

    val taskLists = taskListsScreenState.value.lists
        .filter {
            it.title.contains(search.value, true)
        }
        .toList()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                InputField(
                    value = search.value,
                    placeholder = {
                        Text(
                            text = "Enter search...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic
                        )
                    },
                    onValueChange = {
                        search.value = it
                    }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding)
            ) {
                TaskListsScreenTopHeader(title = "All Task Lists")

                LazyVerticalGrid(
                    modifier = Modifier
                        .weight(1f),
                    columns = GridCells.Fixed(itemsPerRow)
                ) {
                    items(
                        count = taskLists.size,
                        key = { index -> taskLists[index].id }
                    ) { index ->
                        Box(
                            modifier = Modifier
                                .padding(itemPadding.dp)
                                .size(itemSize.width.dp, itemSize.height.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.small
                                )
                                .background(
                                    color = if (taskLists[index].id == selectedList.value?.id) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    },
                                    shape = MaterialTheme.shapes.small
                                )
                                .align(Alignment.CenterHorizontally)
                                .combinedClickable(
                                    onClick = {
                                        navHostController.navigate(
                                            route = ScreenInfo.TaskListScreen(taskLists[index].id)
                                        )
                                    },
                                    onLongClick = {
                                        selectedList.value = taskLists[index]
                                    }
                                ),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = taskLists[index].title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                modifier = Modifier
                    .pointerInput(selectedList.value) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()

                                when {
                                    dragAmount > 0 -> selectedList.value = null
                                    else -> return@detectVerticalDragGestures
                                }
                            }
                        )
                    },
                visible = selectedList.value != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                HorizontalDivider(
                    Modifier.padding(horizontal = 32.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {
                            if (selectedList.value == null) {
                                return@NavigationBarItem
                            }

                            isInputDialogOpen.value = true
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit title"
                            )
                        },
                        label = {
                            Text(
                                text = "Edit title",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                            )
                        },
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {
                            if (selectedList.value == null) {
                                return@NavigationBarItem
                            }

                            isConfirmDialogOpen.value = true
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete list"
                            )
                        },
                        label = {
                            Text(
                                text = "Delete list",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors().copy(
                            selectedIconColor = MaterialTheme.colorScheme.onErrorContainer,
                            selectedIndicatorColor = MaterialTheme.colorScheme.errorContainer,
                        )
                    )
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
        }
    )

    if (isInputDialogOpen.value) {
        InputDialog(
            header = if (selectedList.value == null) "New List" else "Edit List",
            label = "Title",
            placeholder = "Enter title...",
            value = if (selectedList.value == null) "" else selectedList.value!!.title,
            onConfirmRequest = {
                if (it == "") {
                    return@InputDialog
                }

                if (selectedList.value == null) {
                    coroutineScope.launch {
                        val taskListId =
                            taskListsScreenViewModel.handleTaskListTitleChange(title = it)
                        assert(taskListId > 0) { "Task list creation returned $taskListId. This should never happen!" }

                        navHostController.navigate(
                            route = ScreenInfo.TaskListScreen(taskListId)
                        )
                    }
                } else {
                    val id = selectedList.value!!.id

                    coroutineScope.launch {
                        taskListsScreenViewModel.handleTaskListTitleChange(
                            id = id,
                            title = it
                        )
                    }
                }
            },
            onFinally = {
                selectedList.value = null
                isInputDialogOpen.value = false
            }
        )
    }

    if (isConfirmDialogOpen.value && selectedList.value != null) {
        ConfirmDialog(
            imageVector = Icons.Rounded.Warning,
            label = "Are you sure you want to delete the full list? This action cannot be undone!",
            onConfirmRequest = {
                val id = selectedList.value!!.id

                coroutineScope.launch {
                    taskListsScreenViewModel.handleTaskListDelete(id)
                }
            },
            onFinally = {
                selectedList.value = null
                isConfirmDialogOpen.value = false
            }
        )
    }
}