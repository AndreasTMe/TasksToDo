package com.andreast.taskstodo.infrastructure.persistence.dataaccess

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.andreast.taskstodo.domain.TaskListItem
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
}