package com.andreast.taskstodo.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun TaskItemScreen(
    navHostController: NavHostController,
    taskId: String = ""
) {
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
                        var title by remember { mutableStateOf("") }

                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = title,
                            placeholder = {
                                Text(
                                    text = "Title",
                                    fontStyle = FontStyle.Italic
                                )
                            },
                            colors = TextFieldDefaults.noBackground(),
                            onValueChange = {
                                title = it
                            },
                        )
                    }
                    Box {
                        LazyColumn {
                            items(10) {
                                Row {
                                    var checked by remember { mutableStateOf(false) }
                                    val count = it + 1

                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            checked = !checked
                                        }
                                    )
                                    Text(
                                        text = "Task $count",
                                        style = TextStyle(
                                            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
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

@Preview(showBackground = true)
@Composable
private fun TaskItemScreenPreview() {
    TaskItemScreen(navHostController = rememberNavController(), "1")
}