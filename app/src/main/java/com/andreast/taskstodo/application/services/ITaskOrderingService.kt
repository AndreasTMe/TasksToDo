package com.andreast.taskstodo.application.services

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.utils.Level

interface ITaskOrderingService {
    fun calculateOrderForNewItem(parentId: Long?, items: List<TaskListItemDto>): Int
    fun reorderTasks(from: Int, to: Int, items: List<TaskListItemDto>): List<TaskListItemDto>
    fun reorderTasksAfterLevelChange(index: Int, level: Level, items: List<TaskListItemDto>): List<TaskListItemDto>
}