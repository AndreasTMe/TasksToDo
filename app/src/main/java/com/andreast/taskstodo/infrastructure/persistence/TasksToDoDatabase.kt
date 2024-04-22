package com.andreast.taskstodo.infrastructure.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andreast.taskstodo.domain.TaskList
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.infrastructure.persistence.dataaccess.TaskListDao
import com.andreast.taskstodo.infrastructure.persistence.dataaccess.TaskListItemDao

const val DATABASE_NAME = "TASKS_DATABASE"
const val DATABASE_VERSION = 1

@Database(
    entities = [
        TaskList::class,
        TaskListItem::class
    ],
    version = DATABASE_VERSION
)
abstract class TasksToDoDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao
    abstract fun taskListItemDao(): TaskListItemDao
}