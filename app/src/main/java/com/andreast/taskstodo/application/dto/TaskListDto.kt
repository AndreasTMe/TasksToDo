package com.andreast.taskstodo.application.dto

data class TaskListDto(
    var id: Long = 0,
    var title: String = "",
    var items: List<TaskListItemDto> = listOf()
) {
    fun isNew(): Boolean {
        return id.toInt() == 0 && title != ""
    }
}