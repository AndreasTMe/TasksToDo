package com.andreast.taskstodo.application

import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.services.ITaskFamilyService
import com.andreast.taskstodo.application.services.ITaskOrderingService
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.application.services.impl.TaskFamilyService
import com.andreast.taskstodo.application.services.impl.TaskOrderingService
import com.andreast.taskstodo.application.services.impl.TaskScreenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelConfiguration {

    @Provides
    @ViewModelScoped
    fun provideTaskScreenService(repository: ITasksRepository): ITaskScreenService {
        return TaskScreenService(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideTaskFamilyService(): ITaskFamilyService {
        return TaskFamilyService()
    }

    @Provides
    @ViewModelScoped
    fun provideTaskOrderingService(): ITaskOrderingService {
        return TaskOrderingService()
    }
}