package com.andreast.taskstodo.infrastructure.persistence.dataaccess

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListItemIdAndIsCompleted
import com.andreast.taskstodo.domain.TaskListItemIdAndOrder
import com.andreast.taskstodo.domain.TaskListItemIdAndParentId
import com.andreast.taskstodo.domain.TaskListItemIdAndTitle
import com.andreast.taskstodo.domain.TaskListItemTable

@Dao
interface TaskListItemDao {
    @Query(
        """
            SELECT *
            FROM ${TaskListItemTable.NAME}
            WHERE ${TaskListItemTable.COLUMN_TASK_LIST_ID} = :id
            ORDER BY '${TaskListItemTable.COLUMN_ORDER}' ASC
        """
    )
    suspend fun getAllByTaskListId(id: Long): List<TaskListItem>

    @Upsert
    suspend fun upsert(taskListItem: TaskListItem): Long

    @Update(entity = TaskListItem::class)
    suspend fun updateParentId(taskListItem: TaskListItemIdAndParentId)

    @Update(entity = TaskListItem::class)
    suspend fun updateTitle(taskListItem: TaskListItemIdAndTitle)

    @Update(entity = TaskListItem::class)
    suspend fun updateOrder(taskListItem: TaskListItemIdAndOrder)

    @Update(entity = TaskListItem::class)
    suspend fun updateIsCompleted(taskListItem: TaskListItemIdAndIsCompleted)

    @Query("DELETE FROM ${TaskListItemTable.NAME} WHERE ${TaskListItemTable.COLUMN_ID} = :id")
    suspend fun delete(id: Long)
}