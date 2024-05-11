package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.Level
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
        if (from == to || from !in items.indices || to !in items.indices) {
            return emptyList()
        }

        val fromItem = items[from]
        val toItem = items[to]

        if (from < to) {
            var deepestLevel = items[from].level
            var index = from + 1
            while (items[index].level > fromItem.level) {
                if (items[index].level > deepestLevel) {
                    deepestLevel = items[index].level

                    if (deepestLevel + toItem.level > Level.entries.max()) {
                        return emptyList()
                    }
                }

                index++

                if (index > to) {
                    return emptyList()
                }
            }

            if (to < items.size - 1 && items[to + 1].level > toItem.level) {
                return items
                    .filter { item ->
                        item.parentId == toItem.id
                    }
                    .map { item ->
                        item.copy(order = item.order + 1)
                    }
                    .plus(fromItem.copy(parentId = toItem.id, order = 0))
            }

            return items
                .filter { item ->
                    item.parentId == toItem.parentId && item.order > toItem.order
                }
                .map { item ->
                    item.copy(order = item.order + 1)
                }
                .plus(fromItem.copy(parentId = toItem.parentId, order = toItem.order + 1))
        } else {
            // TODO(Implement "reorderTasks")

            return emptyList()
        }
    }
}