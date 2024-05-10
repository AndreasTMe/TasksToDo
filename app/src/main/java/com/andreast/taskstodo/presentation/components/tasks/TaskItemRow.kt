package com.andreast.taskstodo.presentation.components.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.presentation.components.DropdownDivider

@Composable
fun TaskItemRow(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    task: TaskListItemDto,
    onCheckTask: (task: TaskListItemDto) -> Unit = { },
    onEditTask: (task: TaskListItemDto) -> Unit = { },
    onDeleteTask: (task: TaskListItemDto) -> Unit = { },
    onAddSubTask: (parent: TaskListItemDto) -> Unit = { }
) {
    val isDropdownExpanded = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(start = (task.level * 30).dp)
    ) {
        Checkbox(
            modifier = Modifier.width(40.dp),
            checked = task.isCompleted,
            onCheckedChange = {
                onCheckTask(task)
            })
        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onCheckTask(task)
                },
            text = task.title,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            style = MaterialTheme.typography.bodyMedium
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
                    text = {
                        Text(
                            text = "Add sub-task",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
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
                    text = {
                        Text(
                            text = "Edit task",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onEditTask(task)
                        isDropdownExpanded.value = false
                    }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete task")
                    },
                    text = {
                        Text(
                            text = "Delete task",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onDeleteTask(task)
                        isDropdownExpanded.value = false
                    }
                )
            }
        }
    }
}