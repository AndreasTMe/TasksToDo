package com.andreast.taskstodo.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.andreast.taskstodo.application.utils.InsteadOf

object TaskListItemTable {
    const val NAME = "task_list_item"

    const val COLUMN_ID = "_id"
    const val COLUMN_PARENT_ID = "parent_id"
    const val COLUMN_TASK_LIST_ID = "task_list_id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_ORDER = "order"
    const val COLUMN_IS_COMPLETED = "is_completed"
    const val COLUMN_IS_EXPANDED = "is_expanded"
}

@Entity(
    tableName = TaskListItemTable.NAME,
    indices = [Index(value = [TaskListItemTable.COLUMN_ID], unique = true)]
)
data class TaskListItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long = 0,

    @ColumnInfo(name = TaskListItemTable.COLUMN_PARENT_ID)
    val parentId: Long?,

    @ColumnInfo(name = TaskListItemTable.COLUMN_TASK_LIST_ID)
    val taskListId: Long,

    @ColumnInfo(name = TaskListItemTable.COLUMN_TITLE)
    val title: String,

    @ColumnInfo(name = TaskListItemTable.COLUMN_ORDER)
    val order: Int,

    @ColumnInfo(name = TaskListItemTable.COLUMN_IS_COMPLETED)
    val isCompleted: Boolean,
)

data class TaskListItemIdAndTitle(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long,

    @ColumnInfo(name = TaskListItemTable.COLUMN_TITLE)
    val title: String
) : InsteadOf<TaskListItem>

data class TaskListItemIdParentIdAndOrder(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long = 0,

    @ColumnInfo(name = TaskListItemTable.COLUMN_PARENT_ID)
    val parentId: Long?,

    @ColumnInfo(name = TaskListItemTable.COLUMN_ORDER)
    val order: Int
) : InsteadOf<TaskListItem>

data class TaskListItemIdAndIsCompleted(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long = 0,

    @ColumnInfo(name = TaskListItemTable.COLUMN_IS_COMPLETED)
    val isCompleted: Boolean
) : InsteadOf<TaskListItem>