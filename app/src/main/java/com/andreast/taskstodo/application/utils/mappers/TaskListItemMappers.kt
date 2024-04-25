package com.andreast.taskstodo.application.utils.mappers

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListItemIdAndIsCompleted
import com.andreast.taskstodo.domain.TaskListItemIdAndOrder
import com.andreast.taskstodo.domain.TaskListItemIdAndParentId
import com.andreast.taskstodo.domain.TaskListItemIdAndTitle

class TaskListItemMappers {
    companion object {
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

        fun onlyIdAndParentIdToEntity(id: Long, parentId: Long): TaskListItemIdAndParentId {
            return TaskListItemIdAndParentId(id, parentId)
        }

        fun onlyIdAndTitleToEntity(id: Long, title: String): TaskListItemIdAndTitle {
            return TaskListItemIdAndTitle(id, title)
        }

        fun onlyIdAndOrderToEntity(id: Long, order: Int): TaskListItemIdAndOrder {
            return TaskListItemIdAndOrder(id, order)
        }

        fun onlyIdAndIsCompletedToEntity(id: Long, isCompleted: Boolean): TaskListItemIdAndIsCompleted {
            return TaskListItemIdAndIsCompleted(id, isCompleted)
        }
    }
}