package com.andreast.taskstodo.application.persistence

import com.andreast.taskstodo.application.utils.InsteadOf
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListWithItems

interface ITasksRepository {
    suspend fun getTaskLists(): List<TaskList>
    suspend fun getTaskListWithItems(id: Long): TaskListWithItems
    suspend fun upsertTaskList(taskList: TaskList): Long
    suspend fun upsertTaskListItem(taskListItem: TaskListItem): Long
    suspend fun updateTaskListItem(taskListItem: InsteadOf<TaskListItem>)
    suspend fun deleteTaskListItemsById(ids: List<Long>)
}