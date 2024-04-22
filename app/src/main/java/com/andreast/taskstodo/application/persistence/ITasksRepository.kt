package com.andreast.taskstodo.application.persistence

import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListWithItems

interface ITasksRepository {
    suspend fun getTaskLists(): List<TaskList>
    suspend fun getTaskListWithItems(id: Long): TaskListWithItems
    suspend fun insertTaskList(
        taskList: TaskList,
        taskListItems: List<TaskListItem> = listOf()
    )
}