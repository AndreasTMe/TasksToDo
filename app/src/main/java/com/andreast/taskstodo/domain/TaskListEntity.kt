package com.andreast.taskstodo.domain

data class TaskListEntity(
    val id: Long,
    val title: String
    // TODO: val color: String or Color?
) {
    companion object {
        val Invalid: TaskListEntity = TaskListEntity(-1, "Invalid")
    }
}