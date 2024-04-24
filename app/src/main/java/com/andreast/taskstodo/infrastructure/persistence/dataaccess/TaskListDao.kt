package com.andreast.taskstodo.infrastructure.persistence.dataaccess

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListTable

@Dao
interface TaskListDao {
    @Query("SELECT * FROM ${TaskListTable.NAME}")
    suspend fun getAll(): List<TaskList>

    @Query("SELECT * FROM ${TaskListTable.NAME} WHERE ${TaskListTable.COLUMN_ID} = :id LIMIT 1")
    suspend fun getById(id: Long): TaskList

    @Query("SELECT EXISTS(SELECT * FROM ${TaskListTable.NAME} WHERE ${TaskListTable.COLUMN_ID} = :id)")
    suspend fun exists(id: Long): Boolean

    @Upsert
    suspend fun upsert(taskList: TaskList): Long
}