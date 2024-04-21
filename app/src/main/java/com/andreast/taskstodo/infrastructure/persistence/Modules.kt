package com.andreast.taskstodo.infrastructure.persistence

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.andreast.taskstodo.application.persistence.ITaskListRepository
import com.andreast.taskstodo.infrastructure.persistence.repositories.TaskListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityConfiguration {

    @Provides
    @ActivityScoped
    fun provideSQLiteOpenHelper(@ActivityContext context: Context): SQLiteOpenHelper {
        return DbHelper(context)
    }

    @Provides
    @ActivityScoped
    fun provideTaskListRepository(sqLiteOpenHelper: SQLiteOpenHelper): ITaskListRepository {
        return TaskListRepository(sqLiteOpenHelper)
    }
}