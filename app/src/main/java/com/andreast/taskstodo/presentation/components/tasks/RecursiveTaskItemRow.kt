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

@Composable
fun RecursiveTaskItemRow(
    task: TaskListItemDto,
    onCheckTask: (id: Long, isChecked: Boolean) -> Unit,
    onEditTask: (id: Long) -> Unit,
    onDeleteTask: (id: Long) -> Unit,
    onAddSubTask: (id: Long) -> Unit
) {
    val (isCompleted, setIsCompleted) = remember { mutableStateOf(task.isCompleted) }
    val (isExpanded, setIsExpanded) = remember { mutableStateOf(false) }

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
                onCheckTask(task.id, it)
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
        Box {
            IconButton(
                modifier = Modifier.width(40.dp),
                onClick = {
                    setIsExpanded(true)
                }
            ) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Task Options")
            }

            DropdownMenu(
                expanded = isExpanded,
                offset = DpOffset(x = 0.dp, y = 0.dp),
                onDismissRequest = {
                    setIsExpanded(false)
                }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Add, contentDescription = "Add sub-task")
                    },
                    text = { Text("Add sub-task") },
                    onClick = { }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit task")
                    },
                    text = { Text("Edit task") },
                    onClick = { }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete task")
                    },
                    text = { Text("Delete task") },
                    onClick = { }
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