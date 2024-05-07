package com.andreast.taskstodo.application.utils.mappers

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.domain.TaskList

class TaskListMappers {
    companion object {
        fun dtoToEntity(dto: TaskListDto): TaskList {
            return TaskList(
                id = dto.id,
                title = dto.title
            )
        }

        fun entityToDto(entity: TaskList): TaskListDto {
            return TaskListDto(
                id = entity.id,
                title = entity.title
            )
        }
    }
}