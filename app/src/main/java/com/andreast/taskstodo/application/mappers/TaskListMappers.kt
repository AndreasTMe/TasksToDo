package com.andreast.taskstodo.application.mappers

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.domain.TaskList

class TaskListMappers {
    companion object {
        fun dtoToEntity(dto: TaskListDto): TaskList {
            return TaskList(dto.id ?: -1, dto.title ?: "")
        }

        fun entityToDto(entity: TaskList): TaskListDto {
            return TaskListDto(entity.id, entity.title)
        }
    }
}