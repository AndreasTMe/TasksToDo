package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.Level
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskOrderingService

class TaskOrderingService : ITaskOrderingService {
    override fun calculateOrderForNewItem(parentId: Long?, items: List<TaskListItemDto>): Int {
        if (items.isEmpty()) {
            return 0
        }

        return items
            .filter {
                it.parentId == parentId
            }
            .takeIf {
                it.isNotEmpty()
            }
            ?.maxBy {
                it.order
            }
            ?.order?.plus(1) ?: 0
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
            val shouldBecomeChild = to < items.size - 1 && items[to + 1].level > toItem.level
            val targetLevel = if (shouldBecomeChild) items[to + 1].level else toItem.level
            var currentDeepestLevel = items[from].level

            var index = from + 1
            while (items[index].level > fromItem.level) {
                if (items[index].level > currentDeepestLevel) {
                    currentDeepestLevel = items[index].level

                    if (Level.addValues(currentDeepestLevel, targetLevel) > Level.maxValue()) {
                        return emptyList()
                    }
                }

                index++

                if (index > to) {
                    return emptyList()
                }
            }

            if (shouldBecomeChild) {
                return items
                    .filter { item ->
                        item.id != fromItem.id
                                && item.parentId == toItem.id
                    }
                    .map { item ->
                        item.copy(order = item.order + 1)
                    }
                    .plus(fromItem.copy(parentId = toItem.id, order = 0))
            }

            return items
                .filter { item ->
                    item.id != fromItem.id
                            && item.parentId == toItem.parentId
                            && item.order > toItem.order
                }
                .map { item ->
                    item.copy(order = item.order + 1)
                }
                .plus(fromItem.copy(parentId = toItem.parentId, order = toItem.order + 1))
        } else {
            // TODO(Implement "reorderTasks")

            var currentDeepestLevel = items[from].level
            var index = from + 1
            while (index < items.size && items[index].level > fromItem.level) {
                if (items[index].level > currentDeepestLevel) {
                    currentDeepestLevel = items[index].level

                    if (Level.addValues(currentDeepestLevel, toItem.level) > Level.maxValue()) {
                        return emptyList()
                    }
                }

                index++
            }

            return emptyList()
        }
    }
}