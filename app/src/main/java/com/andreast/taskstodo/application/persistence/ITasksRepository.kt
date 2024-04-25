package com.andreast.taskstodo.application.persistence

import com.andreast.taskstodo.application.utils.Fake
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListWithItems

interface ITasksRepository {
    suspend fun getTaskLists(): List<TaskList>
    suspend fun getTaskListWithItems(id: Long): TaskListWithItems
    suspend fun upsertTaskList(taskList: TaskList): Long
    suspend fun upsertTaskListItem(taskListItem: TaskListItem): Long
    suspend fun updateTaskListItem(taskListItem: Fake<TaskListItem>)
    suspend fun deleteTaskListItemById(id: Long)
}