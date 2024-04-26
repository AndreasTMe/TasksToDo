package com.andreast.taskstodo.domain

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.andreast.taskstodo.application.utils.InsteadOf

object TaskListItemTable {
    const val NAME = "task_list_item"

    const val COLUMN_ID = "_id"
    const val COLUMN_PARENT_ID = "parent_id"
    const val COLUMN_TASK_LIST_ID = "task_list_id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_ORDER = "order"
    const val COLUMN_IS_COMPLETED = "is_completed"
}

@Entity(tableName = TaskListItemTable.NAME)
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
    val isCompleted: Boolean
)

data class TaskListItemIdAndParentId(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long,

    @ColumnInfo(name = TaskListItemTable.COLUMN_PARENT_ID)
    val parentId: Long?
) : InsteadOf<TaskListItem>

data class TaskListItemIdAndTitle(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long,

    @ColumnInfo(name = TaskListItemTable.COLUMN_TITLE)
    val title: String
) : InsteadOf<TaskListItem>

data class TaskListItemIdAndOrder(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long = 0,

    @ColumnInfo(name = TaskListItemTable.COLUMN_ORDER)
    val order: Int
) : InsteadOf<TaskListItem>

data class TaskListItemIdAndIsCompleted(
    @ColumnInfo(name = TaskListItemTable.COLUMN_ID)
    val id: Long = 0,

    @ColumnInfo(name = TaskListItemTable.COLUMN_IS_COMPLETED)
    val isCompleted: Boolean
) : InsteadOf<TaskListItem>

data class TaskListWithItems(
    @Embedded
    val taskList: TaskList,

    @Relation(
        parentColumn = TaskListTable.COLUMN_ID,
        entityColumn = TaskListItemTable.COLUMN_TASK_LIST_ID
    )
    val taskListItems: List<TaskListItem>
)