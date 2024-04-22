package com.andreast.taskstodo.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.services.ITaskScreenService
import kotlinx.coroutines.launch

@Composable
fun TaskItemScreen(
    taskScreenService: ITaskScreenService,
    navHostController: NavHostController,
    taskId: String = ""
) {
    val coroutineScope = rememberCoroutineScope()
    val (taskList, setTaskList) = remember { mutableStateOf(TaskListDto()) }

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
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = taskList.title,
                        placeholder = {
                            Text(
                                text = "Title", fontStyle = FontStyle.Italic
                            )
                        },
                        colors = TextFieldDefaults.noBackground(),
                        onValueChange = {
                            taskList.title = it
                        },
                    )
                }
                Box {
                    LazyColumn {
                        coroutineScope.launch {
                            val taskIdParsed = taskId.toLongOrNull()
                            if (taskIdParsed != null) {
                                setTaskList(taskScreenService.getTaskListWithItems(taskIdParsed))
                            }
                        }

                        items(taskList.items.size) {
                            Row {
                                val current = it

                                Checkbox(checked = taskList.items[current].isCompleted,
                                    onCheckedChange = {
                                        taskList.items[current].isCompleted = it
                                    })
                                Text(
                                    text = "Task $current", style = TextStyle(
                                        textDecoration = if (taskList.items[current].isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ), modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
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

@Composable
private fun TextFieldDefaults.noBackground(): TextFieldColors {
    return this.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent
    )
}