package com.andreast.taskstodo.infrastructure.persistence

import android.content.Context
import androidx.room.Room
import com.andreast.taskstodo.BuildConfig
import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.infrastructure.persistence.dataaccess.TaskListDao
import com.andreast.taskstodo.infrastructure.persistence.dataaccess.TaskListItemDao
import com.andreast.taskstodo.infrastructure.persistence.repositories.TasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivityConfiguration {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TasksToDoDatabase {
        val builder = Room.databaseBuilder(
            context,
            TasksToDoDatabase::class.java,
            DATABASE_NAME
        )

        if (BuildConfig.DEBUG) {
            builder.createFromAsset("database/${DATABASE_NAME}.db")
        }

        return builder.build()
    }

    @Provides
    fun provideTaskListDao(database: TasksToDoDatabase): TaskListDao {
        return database.taskListDao()
    }

    @Provides
    fun provideTaskListItemDao(database: TasksToDoDatabase): TaskListItemDao {
        return database.taskListItemDao()
    }

    @Provides
    fun provideTasksRepository(
        taskListDao: TaskListDao,
        taskListItemDao: TaskListItemDao
    ): ITasksRepository {
        return TasksRepository(taskListDao, taskListItemDao)
    }
}