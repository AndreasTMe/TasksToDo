package com.andreast.taskstodo.application.dto

data class TaskListItemDto(
    var id: Long = 0,
    var parentId: Long? = null,
    val taskListId: Long = 0,
    var title: String = "",
    var order: Int = 0,
    var isCompleted: Boolean = false,
    var children: MutableList<TaskListItemDto> = mutableListOf()
)