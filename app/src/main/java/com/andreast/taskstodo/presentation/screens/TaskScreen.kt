package com.andreast.taskstodo.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import java.util.UUID

val ID_LIST = listOf(
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
    UUID.randomUUID().toString(),
)

@Composable
fun TaskScreen(
    navHostController: NavHostController
) {
    Scaffold(
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding)
            ) {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    items(10) {
                        Column(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(8.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color.Gray,
                                    shape = MaterialTheme.shapes.small
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable {
                                    navHostController.navigate(
                                        route = Screen.TaskItemScreen.createRoute(taskId = ID_LIST[it])
                                    )
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val current = ID_LIST[it]
                                Text(
                                    text = "Item $current",
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Add Task")
                },
                onClick = {
                    navHostController.navigate(
                        route = Screen.TaskItemScreen.createRoute()
                    )
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TaskScreenPreview() {
    TaskScreen(navHostController = rememberNavController())
}