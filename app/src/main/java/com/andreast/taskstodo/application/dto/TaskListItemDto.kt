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
    val completedPercentage: Int = 0,
    val isExpanded: Boolean = false,
    val isHidden: Boolean = true,
    val hasChildren: Boolean = false,
) {
    val isCompleted
        get() = completedPercentage == 100

    fun toggle(value: Boolean? = null): TaskListItemDto {
        return if (value == null) {
            this.copy(completedPercentage = if (isCompleted) 0 else 100)
        } else {
            this.copy(completedPercentage = if (value) 100 else 0)
        }
    }
}