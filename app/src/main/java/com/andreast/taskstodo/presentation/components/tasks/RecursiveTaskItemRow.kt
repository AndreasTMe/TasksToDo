package com.andreast.taskstodo.presentation.components.tasks

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.presentation.components.DropdownDivider

@Composable
fun RecursiveTaskItemRow(
    task: TaskListItemDto,
    onCheckTask: (id: Long, isChecked: Boolean) -> Unit,
    onEditTask: (taskToEdit: TaskListItemDto) -> Unit,
    onDeleteTask: (id: Long) -> Unit,
    onAddSubTask: (parent: TaskListItemDto) -> Unit
) {
    val isCompleted = remember { mutableStateOf(task.isCompleted) }
    val isDeleted = remember { mutableStateOf(false) }
    val isDropdownExpanded = remember { mutableStateOf(false) }

    if (isDeleted.value) {
        return
    }

    Row(
        modifier = Modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = DraggableState { /*TODO*/ },
                onDragStarted = {},
                onDragStopped = {}
            )
    ) {
        Checkbox(
            modifier = Modifier.width(40.dp),
            checked = isCompleted.value,
            onCheckedChange = {
                isCompleted.value = it
                onCheckTask(task.id, it)
            })
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            text = task.title,
            style = TextStyle(
                textDecoration = if (isCompleted.value) TextDecoration.LineThrough else TextDecoration.None,
            )
        )
        Box {
            IconButton(
                modifier = Modifier.width(40.dp),
                onClick = {
                    isDropdownExpanded.value = true
                }
            ) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Task Options")
            }

            DropdownMenu(
                expanded = isDropdownExpanded.value,
                offset = DpOffset(x = 0.dp, y = 0.dp),
                onDismissRequest = {
                    isDropdownExpanded.value = false
                }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Add, contentDescription = "Add sub-task")
                    },
                    text = { Text("Add sub-task") },
                    onClick = {
                        onAddSubTask(task)
                        isDropdownExpanded.value = false
                    }
                )

                DropdownDivider()

                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit task")
                    },
                    text = { Text("Edit task") },
                    onClick = {
                        onEditTask(task)
                        isDropdownExpanded.value = false
                    }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete task")
                    },
                    text = { Text("Delete task") },
                    onClick = {
                        onDeleteTask(task.id)
                        isDeleted.value = true
                        isDropdownExpanded.value = false
                    }
                )
            }
        }
    }

    if (task.children.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(start = 30.dp)
        ) {
            for (child in task.children) {
                RecursiveTaskItemRow(
                    child,
                    onCheckTask,
                    onEditTask,
                    onDeleteTask,
                    onAddSubTask
                )
            }
        }
    }
}