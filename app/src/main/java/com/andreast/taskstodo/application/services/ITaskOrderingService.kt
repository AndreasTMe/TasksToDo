package com.andreast.taskstodo.application.services

import com.andreast.taskstodo.application.dto.TaskListItemDto

interface ITaskOrderingService {
    fun calculateOrder(parentId: Long?, items: List<TaskListItemDto>): Int
    fun reorderTasks(from: Int, to: Int, items: List<TaskListItemDto>): List<TaskListItemDto>
}