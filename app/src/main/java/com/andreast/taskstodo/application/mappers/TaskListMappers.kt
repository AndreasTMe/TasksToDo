package com.andreast.taskstodo.application.mappers

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.domain.TaskListEntity

class TaskListMappers {
    companion object {
        fun dtoToEntity(dto: TaskListDto): TaskListEntity {
            return TaskListEntity(dto.id ?: -1, dto.title ?: "")
        }

        fun entityToDto(entity: TaskListEntity): TaskListDto {
            return TaskListDto(entity.id, entity.title)
        }
    }
}