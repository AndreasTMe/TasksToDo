package com.andreast.taskstodo.presentation.components.tasks

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.utils.Level
import com.andreast.taskstodo.presentation.components.DropdownDivider

@Composable
fun TaskItemRow(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    task: TaskListItemDto,
    isExpanded: Boolean? = null,
    onCheckTask: (task: TaskListItemDto) -> Unit = { },
    onEditTask: (task: TaskListItemDto) -> Unit = { },
    onDeleteTask: (task: TaskListItemDto) -> Unit = { },
    onAddSubTask: (parent: TaskListItemDto) -> Unit = { },
    onLevelChange: (level: Level) -> Unit = { },
    onExpandSubtasks: () -> Unit = { }
) {
    val dragState = remember { mutableIntStateOf(0) }
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }

    val paddingStart = animateDpAsState(
        label = "",
        targetValue = (when {
            dragState.intValue > 0 -> task.level + 1
            dragState.intValue < 0 -> task.level - 1
            else -> task.level
        } * 30).dp
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(start = paddingStart.value)
            .pointerInput(task.level) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()

                        if (dragState.intValue != 0) {
                            return@detectHorizontalDragGestures
                        }

                        when {
                            dragAmount > 0 && task.level < Level.maxValue() -> {
                                dragState.intValue = 1
                            }

                            dragAmount < 0 && task.level > 0 -> {
                                dragState.intValue = -1
                            }

                            else -> return@detectHorizontalDragGestures
                        }
                    },
                    onDragStart = {
                        dragState.intValue = 0
                    },
                    onDragEnd = {
                        if (dragState.intValue != 0) {
                            onLevelChange(task.level + dragState.intValue)
                        }
                        dragState.intValue = 0
                    },
                    onDragCancel = {
                        dragState.intValue = 0
                    }
                )
            }
    ) {
        if (isExpanded != null && task.hasChildren) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .align(Alignment.CenterVertically)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        onExpandSubtasks()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Task Options"
                )
            }
        } else {
            Box(
                modifier = Modifier.width(20.dp)
            )
        }
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
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onCheckTask(task)
                },
            text = task.title,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            style = MaterialTheme.typography.bodyMedium
        )
        if (task.hasChildren && task.completedPercentage in 0..100) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(if (task.level == Level.Zero) 30.dp else 20.dp),
                    progress = { task.completedPercentage * 0.01f },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
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
                if (task.level < Level.maxValue()) {
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
                }

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