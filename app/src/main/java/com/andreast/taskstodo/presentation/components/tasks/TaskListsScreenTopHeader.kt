package com.andreast.taskstodo.presentation.components.tasks

import androidx.compose.runtime.Composable
import com.andreast.taskstodo.presentation.components.headers.GenericTopHeader

@Composable
fun TaskListsScreenTopHeader(
    title: String
) {
    GenericTopHeader(
        title = title
    ) {
        // TODO: Add settings button
    }
}