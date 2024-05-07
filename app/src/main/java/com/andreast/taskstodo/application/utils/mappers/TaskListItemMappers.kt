package com.andreast.taskstodo.application.utils.mappers

import com.andreast.taskstodo.application.dto.Level
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
                id = entity.id,
                parentId = entity.parentId,
                parentLevel = Level.None,
                taskListId = entity.taskListId,
                title = entity.title,
                level = Level.Zero,
                order = entity.order,
                isCompleted = entity.isCompleted,
            )
        }

        fun dtoToEntity(dto: TaskListItemDto): TaskListItem {
            return TaskListItem(
                id = dto.id,
                parentId = dto.parentId,
                taskListId = dto.taskListId,
                title = dto.title,
                order = dto.order,
                isCompleted = dto.isCompleted
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

        fun onlyIdAndIsCompletedToEntity(
            id: Long,
            isCompleted: Boolean
        ): TaskListItemIdAndIsCompleted {
            return TaskListItemIdAndIsCompleted(id, isCompleted)
        }
    }
}