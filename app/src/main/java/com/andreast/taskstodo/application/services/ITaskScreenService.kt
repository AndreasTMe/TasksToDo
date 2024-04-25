package com.andreast.taskstodo.application.services

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto

interface ITaskScreenService {
    suspend fun getAllTaskLists(): List<TaskListDto>
    suspend fun getTaskListWithItems(id: Long): TaskListDto
    suspend fun upsertTaskList(taskList: TaskListDto): Long
    suspend fun upsertTaskListItem(taskListItem: TaskListItemDto): Long
    suspend fun updateTaskListItemTitle(id: Long, title: String)
    suspend fun updateTaskListItemParentId(id: Long, parentId: Long)
    suspend fun updateTaskListItemOrder(id: Long, order: Int)
    suspend fun updateTaskListItemCompletedState(id: Long, isCompleted: Boolean)
}