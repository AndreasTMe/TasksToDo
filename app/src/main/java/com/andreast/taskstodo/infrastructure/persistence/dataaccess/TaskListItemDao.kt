package com.andreast.taskstodo.infrastructure.persistence.dataaccess

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andreast.taskstodo.domain.TaskList
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

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(taskList: TaskList, taskListItems: List<TaskListItem>)
}