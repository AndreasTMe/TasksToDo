package com.andreast.taskstodo.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object TaskListTable {
    const val NAME = "task_list"

    const val COLUMN_ID = "_id"
    const val COLUMN_TITLE = "title"
    // TODO: const val COLUMN_COLOR = "color"
}

@Entity(
    tableName = TaskListTable.NAME,
    indices = [Index(value = [TaskListTable.COLUMN_ID], unique = true)]
)
data class TaskList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TaskListTable.COLUMN_ID)
    val id: Long = 0,

    @ColumnInfo(name = TaskListTable.COLUMN_TITLE)
    val title: String
)