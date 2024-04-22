package com.andreast.taskstodo.infrastructure.persistence.dataaccess

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.andreast.taskstodo.domain.TaskListTable
import com.andreast.taskstodo.domain.TaskListWithItems

@Dao
interface TaskListItemDao {
    @Transaction
    @Query("SELECT * FROM ${TaskListTable.NAME} WHERE ${TaskListTable.COLUMN_ID} = :id LIMIT 1")
    suspend fun getAllByTaskListId(id: Long): TaskListWithItems
}