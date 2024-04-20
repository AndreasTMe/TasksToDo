package com.andreast.taskstodo.infrastructure.persistence.repositories

import android.database.sqlite.SQLiteDatabase
import com.andreast.taskstodo.application.persistence.IRepository
import com.andreast.taskstodo.domain.TaskList

class TaskListRepository(
    db: SQLiteDatabase
) : IRepository<TaskList, Int> {
    private val _db: SQLiteDatabase

    init {
        _db = db
        assert(_db.isOpen) { "Database is not open" }
    }

    override suspend fun getAll(): List<TaskList> {
        return listOf(
            TaskList(1, "temp 1"),
            TaskList(2, "temp 2"),
            TaskList(3, "temp 3")
        )
    }

    override suspend fun create(item: TaskList): TaskList {
        return TaskList(1, "temp")
    }

    override suspend fun update(item: TaskList): TaskList {
        return TaskList(1, "temp")
    }

    override suspend fun delete(id: Int): Boolean {
        return true
    }
}