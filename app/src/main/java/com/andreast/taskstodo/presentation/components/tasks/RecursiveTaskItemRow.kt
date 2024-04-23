package com.andreast.taskstodo.presentation.components.tasks

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.andreast.taskstodo.application.dto.TaskListItemDto

@Composable
fun RecursiveTaskItemRow(
    index: Int,
    task: TaskListItemDto,
    onItemChecked: (indices: List<Int>) -> Unit
) {
    RecursiveTaskItemRow(task, mutableListOf(index), onItemChecked)
}

@Composable
private fun RecursiveTaskItemRow(
    task: TaskListItemDto,
    indices: List<Int>,
    onItemChecked: (indexTree: List<Int>) -> Unit
) {
    val (isCompleted, setIsCompleted) = remember { mutableStateOf(task.isCompleted) }

    Row(
        modifier = Modifier.draggable(
            orientation = Orientation.Horizontal,
            state = DraggableState { /*TODO*/ },
            onDragStarted = {},
            onDragStopped = {}
        )
    ) {
        Checkbox(
            modifier = Modifier.width(40.dp),
            checked = isCompleted,
            onCheckedChange = {
                setIsCompleted(it)
                onItemChecked(indices)
            })
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            text = task.title,
            style = TextStyle(
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            )
        )
        IconButton(
            modifier = Modifier.width(40.dp),
            onClick = {}
        ) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Task Options")
        }
    }

    if (task.children.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(start = 30.dp)
        ) {
            for ((childIndex, child) in task.children.withIndex()) {
                RecursiveTaskItemRow(child, indices + childIndex, onItemChecked)
            }
        }
    }
}