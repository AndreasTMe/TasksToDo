package com.andreast.taskstodo.application.dto

data class TaskListDto(
    var id: Long = -1,
    var title: String = "",
    var items: List<TaskListItemDto> = listOf()
)