package com.andreast.taskstodo.infrastructure.persistence.repositories

import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.utils.Fake
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListItemIdAndIsCompleted
import com.andreast.taskstodo.domain.TaskListItemIdAndOrder
import com.andreast.taskstodo.domain.TaskListItemIdAndParentId
import com.andreast.taskstodo.domain.TaskListItemIdAndTitle
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
        val taskList = taskListDao.getById(id)
        val taskListItems = taskListItemDao.getAllByTaskListId(id)

        return TaskListWithItems(taskList, taskListItems)
    }

    override suspend fun upsertTaskList(taskList: TaskList): Long {
        return taskListDao.upsert(taskList)
    }

    override suspend fun upsertTaskListItem(taskListItem: TaskListItem): Long {
        if (!taskListDao.exists(taskListItem.taskListId)) {
            return -1
        }

        return taskListItemDao.upsert(taskListItem)
    }

    override suspend fun updateTaskListItem(taskListItem: Fake<TaskListItem>) {
        when (taskListItem) {
            is TaskListItemIdAndParentId -> taskListItemDao.updateParentId(taskListItem)
            is TaskListItemIdAndTitle -> taskListItemDao.updateTitle(taskListItem)
            is TaskListItemIdAndOrder -> taskListItemDao.updateOrder(taskListItem)
            is TaskListItemIdAndIsCompleted -> taskListItemDao.updateIsCompleted(taskListItem)
        }
    }
}