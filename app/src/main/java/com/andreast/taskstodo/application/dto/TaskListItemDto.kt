package com.andreast.taskstodo.application.dto

data class TaskListItemDto(
    val id: Long = 0,
    val parentId: Long? = null,
    val taskListId: Long = 0,
    val title: String = "",
    val order: Int = 0,
    val isCompleted: Boolean = false,
    val children: MutableList<TaskListItemDto> = mutableListOf()
)