package com.andreast.taskstodo.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.presentation.components.InputDialog
import com.andreast.taskstodo.presentation.components.InputField
import com.andreast.taskstodo.presentation.components.tasks.TaskListsScreenTopHeader
import kotlinx.coroutines.launch

@Composable
fun TaskListsScreen(
    taskListsScreenViewModel: TaskListsScreenViewModel,
    navHostController: NavHostController
) {
    val taskListsScreenState = taskListsScreenViewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val search = remember { mutableStateOf("") }
    val isInputDialogOpen = remember { mutableStateOf(false) }

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
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .align(Alignment.CenterHorizontally)
                                .clickable {
                                    navHostController.navigate(
                                        route = ScreenInfo.TaskListScreen.createRoute(
                                            taskListId = taskLists[index].id.toString()
                                        )
                                    )
                                },
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp),
                                text = taskLists[index].title
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
                    header = "New List",
                    label = "Title",
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
}