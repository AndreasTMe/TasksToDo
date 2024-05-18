package com.andreast.taskstodo.application.utils.mappers

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.utils.Level
import com.andreast.taskstodo.domain.TaskListItem
import com.andreast.taskstodo.domain.TaskListItemIdAndIsCompleted
import com.andreast.taskstodo.domain.TaskListItemIdAndIsExpanded
import com.andreast.taskstodo.domain.TaskListItemIdAndTitle
import com.andreast.taskstodo.domain.TaskListItemIdParentIdAndOrder

class TaskListItemMappers {
    companion object {
        fun entityToDto(entity: TaskListItem, isHidden: Boolean = true): TaskListItemDto {
            return TaskListItemDto(
                id = entity.id,
                parentId = entity.parentId,
                parentLevel = Level.None,
                taskListId = entity.taskListId,
                title = entity.title,
                level = Level.Zero,
                order = entity.order,
                completedPercentage = if (entity.isCompleted) 100 else 0,
                isExpanded = entity.isExpanded,
                isHidden = isHidden
            )
        }

        fun dtoToEntity(dto: TaskListItemDto): TaskListItem {
            return TaskListItem(
                id = dto.id,
                parentId = dto.parentId,
                taskListId = dto.taskListId,
                title = dto.title,
                order = dto.order,
                isCompleted = dto.isCompleted,
                isExpanded = dto.isExpanded
            )
        }

        fun onlyIdAndTitleToEntity(id: Long, title: String): TaskListItemIdAndTitle {
            return TaskListItemIdAndTitle(id, title)
        }

        fun onlyIdParentIdAndOrderToEntity(
            id: Long,
            parentId: Long?,
            order: Int
        ): TaskListItemIdParentIdAndOrder {
            return TaskListItemIdParentIdAndOrder(id, parentId, order)
        }

        fun onlyIdAndIsCompletedToEntity(
            id: Long,
            isCompleted: Boolean
        ): TaskListItemIdAndIsCompleted {
            return TaskListItemIdAndIsCompleted(id, isCompleted)
        }

        fun onlyIdAndIsExpandedToEntity(
            id: Long,
            isExpanded: Boolean
        ): TaskListItemIdAndIsExpanded {
            return TaskListItemIdAndIsExpanded(id, isExpanded)
        }
    }
}