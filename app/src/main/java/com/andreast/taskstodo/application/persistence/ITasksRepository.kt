package com.andreast.taskstodo.application.persistence

import com.andreast.taskstodo.application.utils.InsteadOf
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem

interface ITasksRepository {
    suspend fun getTaskLists(): List<TaskList>
    suspend fun getTaskListById(id: Long): TaskList
    suspend fun getTaskListItemsByListId(id: Long): List<TaskListItem>
    suspend fun upsertTaskList(taskList: TaskList): Long
    suspend fun upsertTaskListItem(taskListItem: TaskListItem): Long
    suspend fun updateTaskListItems(vararg taskListItems: InsteadOf<TaskListItem>)
    suspend fun deleteTaskListById(id: Long)
    suspend fun deleteTaskListItemsById(ids: List<Long>)
}