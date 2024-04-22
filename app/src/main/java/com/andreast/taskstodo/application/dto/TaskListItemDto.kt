package com.andreast.taskstodo.application.dto

import com.andreast.taskstodo.domain.TaskListItem

data class TaskListItemDto(
    var id: Long = -1,
    var title: String = "",
    var order: Int = -1,
    var isCompleted: Boolean = false,
    var children: List<TaskListItem> = listOf()
)