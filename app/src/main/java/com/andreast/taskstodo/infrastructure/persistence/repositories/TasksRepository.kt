package com.andreast.taskstodo.infrastructure.persistence.repositories

import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.utils.InsteadOf
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListItemIdAndIsCompleted
import com.andreast.taskstodo.domain.TaskListItemIdAndOrder
import com.andreast.taskstodo.domain.TaskListItemIdAndParentId
import com.andreast.taskstodo.domain.TaskListItemIdAndTitle
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

    override suspend fun getTaskListById(id: Long): TaskList {
        return taskListDao.getById(id)
    }

    override suspend fun getTaskListItemsByListId(id: Long): List<TaskListItem> {
        return taskListItemDao.getAllByTaskListId(id)
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

    override suspend fun updateTaskListItems(vararg taskListItems: InsteadOf<TaskListItem>) {
        if (taskListItems.isEmpty()) {
            return
        }

        when (taskListItems[0]) {
            is TaskListItemIdAndParentId -> taskListItemDao.updateParentIds(
                taskListItems[0] as TaskListItemIdAndParentId
            )

            is TaskListItemIdAndTitle -> taskListItemDao.updateTitles(
                taskListItems[0] as TaskListItemIdAndTitle
            )

            is TaskListItemIdAndOrder -> taskListItemDao.updateOrders(
                taskListItems[0] as TaskListItemIdAndOrder
            )

            is TaskListItemIdAndIsCompleted -> taskListItemDao.updateCompletedStates(
                *taskListItems
                    .map {
                        it as TaskListItemIdAndIsCompleted
                    }
                    .toTypedArray()
            )
        }
    }

    override suspend fun deleteTaskListById(id: Long) {
        taskListDao.deleteById(id)
        taskListItemDao.deleteAllByTaskListId(id)
    }

    override suspend fun deleteTaskListItemsById(ids: List<Long>) {
        taskListItemDao.deleteAllById(ids)
    }
}