package com.andreast.taskstodo.infrastructure.persistence.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import com.andreast.taskstodo.application.persistence.ITaskListRepository
import com.andreast.taskstodo.domain.TaskListEntity
import com.andreast.taskstodo.infrastructure.persistence.tables.TaskList
import javax.inject.Inject

class TaskListRepository @Inject constructor(
    private val db: SQLiteOpenHelper
) : ITaskListRepository {
    override suspend fun getAll(): List<TaskListEntity> {
        val database = db.readableDatabase

        val cursor = database.query(
            TaskList.Table.NAME,
            arrayOf(TaskList.Table.COLUMN_ID, TaskList.Table.COLUMN_TITLE),
            null,
            null,
            null,
            null,
            "${TaskList.Table.COLUMN_ID} ASC"
        )

        val taskListEntities = mutableListOf<TaskListEntity>()
        with(cursor) {
            while (moveToNext()) {
                val taskListEntity = TaskListEntity(
                    getLong(getColumnIndexOrThrow(TaskList.Table.COLUMN_ID)),
                    getString(getColumnIndexOrThrow(TaskList.Table.COLUMN_TITLE))
                )
                taskListEntities.add(taskListEntity)
            }
        }

        cursor.close()

        return taskListEntities
    }

    override suspend fun create(entity: TaskListEntity): TaskListEntity {
        val values = ContentValues().apply {
            put(TaskList.Table.COLUMN_TITLE, entity.title)
        }

        val database = db.writableDatabase
        val newRowId = database.insert(TaskList.Table.NAME, null, values)

        return if (newRowId >= 0) TaskListEntity(newRowId, entity.title) else TaskListEntity.Invalid
    }

    override suspend fun update(entity: TaskListEntity): TaskListEntity {
        assert(entity.id >= 0) { "Invalid id passed for update" }

        val values = ContentValues().apply {
            put(TaskList.Table.COLUMN_TITLE, entity.title)
        }

        val database = db.writableDatabase
        val count = database.update(
            TaskList.Table.NAME,
            values,
            "${TaskList.Table.COLUMN_ID} LIKE ?",
            arrayOf(entity.id.toString())
        )

        return if (count == 1) entity else TaskListEntity.Invalid
    }

    override suspend fun delete(id: Long): Boolean {
        assert(id >= 0) { "Invalid id passed for deletion" }

        val database = db.writableDatabase
        val deletedRows = database.delete(
            TaskList.Table.NAME,
            "${TaskList.Table.COLUMN_ID} LIKE ?",
            arrayOf(id.toString())
        )

        return deletedRows == 1
    }
}