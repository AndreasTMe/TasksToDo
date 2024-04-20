package com.andreast.taskstodo.infrastructure.persistence

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.andreast.taskstodo.application.persistence.IDbContext
import com.andreast.taskstodo.application.persistence.IRepository
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.infrastructure.persistence.repositories.TaskListRepository

class DbContext(
    context: Context
) : IDbContext {
    private val _sqLiteOpenHelper: SQLiteOpenHelper

    private var _taskListRepository: IRepository<TaskList, Int>? = null

    init {
        _sqLiteOpenHelper = DbHelper(context)
    }

    override val taskLists: IRepository<TaskList, Int>
        get() {
            if (_taskListRepository == null) {
                _taskListRepository = TaskListRepository(_sqLiteOpenHelper.writableDatabase)
            }
            return _taskListRepository!!
        }

    override fun close() {
        try {
            _sqLiteOpenHelper.close()
        } catch (ex: Exception) {
            ex.message?.let { Log.e("DB_ERROR", it) }
        }
    }
}