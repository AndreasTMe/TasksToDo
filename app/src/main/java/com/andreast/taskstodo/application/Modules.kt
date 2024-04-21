package com.andreast.taskstodo.application

import com.andreast.taskstodo.application.persistence.ITaskListRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.application.services.impl.TaskScreenServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityConfiguration {

    @Provides
    @ActivityScoped
    fun provideTaskScreenService(repository: ITaskListRepository): ITaskScreenService {
        return TaskScreenServiceImpl(repository)
    }
}