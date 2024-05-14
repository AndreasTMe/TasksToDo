package com.andreast.taskstodo.application.dto

import com.andreast.taskstodo.application.utils.Level

data class TaskListItemDto(
    val id: Long = 0,
    val parentId: Long? = null,
    val parentLevel: Level = Level.Zero,
    val taskListId: Long = 0,
    val title: String = "",
    val level: Level = Level.Zero,
    val order: Int = 0,
    val isCompleted: Boolean = false
)