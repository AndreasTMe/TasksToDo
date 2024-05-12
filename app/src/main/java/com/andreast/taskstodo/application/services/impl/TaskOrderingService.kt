package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.Level
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskOrderingService

class TaskOrderingService : ITaskOrderingService {
    override fun calculateOrderForNewItem(parentId: Long?, items: List<TaskListItemDto>): Int {
        if (items.isEmpty()) {
            return 0
        }

        if (items.none { it.id == parentId }) {
            return -1
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
        if (from !in items.indices || to !in items.indices) {
            return emptyList()
        }

        val fromItem = items[from]
        val toItem = items[to]

        if (from < to) {
            val shouldBecomeChild = to < items.size - 1 && items[to + 1].level > toItem.level

            if (canReorderWhenMovingDown(from, to, items, shouldBecomeChild)) {
                if (shouldBecomeChild) {
                    var fromLevelOrder = 0
                    var toLevelOrder = 1

                    return items
                        .filter { item ->
                            item.parentId == fromItem.parentId || item.parentId == toItem.id
                        }
                        .sortedBy { it.order }
                        .map {
                            if (it.id == fromItem.id) {
                                return@map it.copy(parentId = toItem.id, order = 0)
                            } else if (it.parentId == fromItem.parentId) {
                                return@map it.copy(order = fromLevelOrder++)
                            } else {
                                return@map it.copy(order = toLevelOrder++)
                            }
                        }
                }

                return items
                    .filter { item ->
                        item.parentId == toItem.parentId
                    }
                    .sortedBy { it.order }
                    .toMutableList()
                    .apply {
                        val insertionIndex = indexOfFirst { it.id == toItem.id } + 1

                        if (fromItem.parentId == toItem.parentId) {
                            add(insertionIndex, fromItem)
                            removeAt(indexOfFirst { it.id == fromItem.id })
                        } else {
                            add(insertionIndex, fromItem.copy(parentId = toItem.parentId))
                        }
                    }
                    .mapIndexed { i, item -> item.copy(order = i) }
            }
        } else if (from > to) {
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

        return emptyList()
    }

    internal fun canReorderWhenMovingDown(
        from: Int,
        to: Int,
        items: List<TaskListItemDto>,
        shouldBecomeChild: Boolean
    ): Boolean {
        val targetLevel = if (shouldBecomeChild) items[to + 1].level else items[to].level
        var currentDeepestLevel = items[from].level

        var index = from + 1
        while (items[index].level > items[from].level) {
            if (items[index].level > currentDeepestLevel) {
                currentDeepestLevel = items[index].level

                if (Level.addValues(currentDeepestLevel, targetLevel) > Level.maxValue()) {
                    return false
                }
            }

            index++

            if (index > to) {
                return false
            }
        }

        return true
    }
}