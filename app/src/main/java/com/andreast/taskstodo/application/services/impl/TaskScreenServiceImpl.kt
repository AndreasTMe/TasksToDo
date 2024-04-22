package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.mappers.TaskListMappers
import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import java.util.stream.Collectors
import javax.inject.Inject

class TaskScreenServiceImpl @Inject constructor(
    private val repository: ITasksRepository
) : ITaskScreenService {

    override suspend fun getAllTaskLists(): List<TaskListDto> {
        return repository.getTaskLists()
            .stream()
            .map { entity ->
                TaskListMappers.entityToDto(entity)
            }
            .collect(Collectors.toList())
    }

    override suspend fun getTaskListWithItems(id: Long): TaskListDto {
        // TODO: Temporary
        return TaskListDto(
            1,
            "Test",
            listOf(
                TaskListItemDto(1, "Item 1", 1, false, listOf()),
                TaskListItemDto(2, "Item 2", 2, true, listOf()),
                TaskListItemDto(3, "Item 3", 3, false, listOf()),
                TaskListItemDto(4, "Item 4", 4, false, listOf()),
                TaskListItemDto(5, "Item 5", 5, true, listOf()),
                TaskListItemDto(6, "Item 6", 6, true, listOf()),
            )
        )
    }
}