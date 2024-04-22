package com.andreast.taskstodo.application.mappers

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.domain.TaskListItem

class TaskListItemMappers {
    companion object {
        fun dtoToEntity(dto: TaskListItemDto): TaskListItem {
            return TaskListItem(
                dto.id,
                dto.parentId,
                dto.taskListId,
                dto.title,
                dto.order,
                dto.isCompleted
            )
        }

        fun entityToDto(entity: TaskListItem): TaskListItemDto {
            return TaskListItemDto(
                entity.id,
                entity.parentId,
                entity.taskListId,
                entity.title,
                entity.order,
                entity.isCompleted
            )
        }
    }
}