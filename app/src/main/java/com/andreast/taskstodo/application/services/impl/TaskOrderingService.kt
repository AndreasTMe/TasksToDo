package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskOrderingService

class TaskOrderingService : ITaskOrderingService {
    override fun calculateOrder(parentId: Long?, items: List<TaskListItemDto>): Int {
        if (items.isEmpty()) {
            return 0
        }

        return items
            .filter {
                it.parentId == parentId
            }
            .maxBy {
                it.order
            }
            .order.plus(1)
    }

    override fun reorderTasks(
        from: Int,
        to: Int,
        items: List<TaskListItemDto>
    ): List<TaskListItemDto> {
        // TODO(Implement)
        return emptyList()
    }
}