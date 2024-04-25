package com.andreast.taskstodo.application.dto

data class TaskListDto(
    var id: Long = 0,
    var title: String = "",
    var items: MutableList<TaskListItemDto> = mutableListOf()
)