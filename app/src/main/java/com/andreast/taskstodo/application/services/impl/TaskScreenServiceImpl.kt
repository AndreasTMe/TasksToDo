package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.mappers.TaskListMappers
import com.andreast.taskstodo.application.persistence.ITaskListRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import java.util.stream.Collectors
import javax.inject.Inject

class TaskScreenServiceImpl @Inject constructor(
    private val repository: ITaskListRepository
) : ITaskScreenService {

    override suspend fun getAllTaskLists(): List<TaskListDto> {
        return repository.getAll()
            .stream()
            .map { entity ->
                TaskListMappers.entityToDto(entity)
            }
            .collect(Collectors.toList())
    }
}