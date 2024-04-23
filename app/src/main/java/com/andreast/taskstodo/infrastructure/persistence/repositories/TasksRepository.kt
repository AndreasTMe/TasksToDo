package com.andreast.taskstodo.infrastructure.persistence.repositories

import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListWithItems
import com.andreast.taskstodo.infrastructure.persistence.dataaccess.TaskListDao
import com.andreast.taskstodo.infrastructure.persistence.dataaccess.TaskListItemDao
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val taskListDao: TaskListDao,
    private val taskListItemDao: TaskListItemDao
) : ITasksRepository {
    override suspend fun getTaskLists(): List<TaskList> {
        return taskListDao.getAll()
    }

    override suspend fun getTaskListWithItems(id: Long): TaskListWithItems {
        return TaskListWithItems(
            taskList = taskListDao.getById(id),
            taskListItems = taskListItemDao.getAllByTaskListId(id)
        )
    }

    override suspend fun insertTaskList(
        taskList: TaskList,
        taskListItems: List<TaskListItem>
    ) {
        if (taskListItems.isEmpty()) {
            taskListDao.insert(taskList)
        } else {
            taskListItemDao.insert(taskList, taskListItems)
        }
    }
}