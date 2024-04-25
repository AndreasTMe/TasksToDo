package com.andreast.taskstodo.application.utils.mappers

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.domain.TaskList

class TaskListMappers {
    companion object {
        fun dtoToEntity(dto: TaskListDto): TaskList {
            return TaskList(dto.id, dto.title)
        }

        fun entityToDto(entity: TaskList): TaskListDto {
            return TaskListDto(entity.id, entity.title)
        }
    }
}