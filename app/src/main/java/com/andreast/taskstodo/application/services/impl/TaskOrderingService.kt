package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.Level
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskOrderingService

class TaskOrderingService : ITaskOrderingService {
    override fun calculateOrderForNewItem(parentId: Long?, items: List<TaskListItemDto>): Int {
        if (items.isEmpty()) {
            return 0
        }

        if (parentId != null && items.none { it.id == parentId }) {
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
        val shouldBecomeChild = checkIfShouldBecomeChild(from, to, items)

        return if (from < to && canReorderWhenMovingDown(from, to, items, shouldBecomeChild)) {
            if (shouldBecomeChild) {
                items.getReorderedWhenMovingDownAsChild(fromItem, toItem)
            } else {
                items.getReorderedWhenMovingDown(fromItem, toItem)
            }
        } else if (from > to && canReorderWhenMovingUp(from, to, items, shouldBecomeChild)) {
            if (shouldBecomeChild) {
                items.getReorderedWhenMovingUpAsChild(fromItem, toItem)
            } else {
                items.getReorderedWhenMovingUp(fromItem, toItem)
            }
        } else {
            emptyList()
        }
    }

    private fun checkIfShouldBecomeChild(
        from: Int,
        to: Int,
        items: List<TaskListItemDto>
    ): Boolean {
        return if (from < to) {
            to < items.size - 1 && items[to + 1].level > items[to].level
        } else {
            to > 0 && items[from].level != items[to].level && items[to].level > items[to - 1].level
        }
    }

    internal fun canReorderWhenMovingDown(
        from: Int,
        to: Int,
        items: List<TaskListItemDto>,
        shouldBecomeChild: Boolean
    ): Boolean {
        return canReorder(from, to, items, shouldBecomeChild) { checkedIndex, toIndex ->
            checkedIndex > toIndex
        }
    }

    internal fun canReorderWhenMovingUp(
        from: Int,
        to: Int,
        items: List<TaskListItemDto>,
        shouldBecomeChild: Boolean
    ): Boolean {
        return canReorder(from, to, items, shouldBecomeChild)
    }

    private fun canReorder(
        from: Int,
        to: Int,
        items: List<TaskListItemDto>,
        shouldBecomeChild: Boolean,
        shouldFailWhen: (checkedIndex: Int, toIndex: Int) -> Boolean = { _, _ -> false }
    ): Boolean {
        val targetLevel = if (shouldBecomeChild) items[to + 1].level else items[to].level
        var currentDeepestLevel = items[from].level

        var index = from + 1
        while (index < items.size && items[index].level > items[from].level) {
            if (items[index].level > currentDeepestLevel) {
                currentDeepestLevel = items[index].level

                if (Level.addValues(currentDeepestLevel, targetLevel) > Level.maxValue()) {
                    return false
                }
            }

            index++

            if (shouldFailWhen(index, to)) {
                return false
            }
        }

        return true
    }
}

private inline fun List<TaskListItemDto>.filterAndSortByOrder(predicate: (TaskListItemDto) -> Boolean): List<TaskListItemDto> {
    return this.filter(predicate).sortedBy { it.order }
}

private fun List<TaskListItemDto>.getReorderedWhenMovingDown(
    fromItem: TaskListItemDto,
    toItem: TaskListItemDto
): List<TaskListItemDto> {
    return this
        .filterAndSortByOrder { item ->
            item.parentId == toItem.parentId
        }
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

private fun List<TaskListItemDto>.getReorderedWhenMovingDownAsChild(
    fromItem: TaskListItemDto,
    toItem: TaskListItemDto
): List<TaskListItemDto> {
    var fromLevelOrder = 0
    var toLevelOrder = 1

    return this
        .filterAndSortByOrder { item ->
            item.parentId == fromItem.parentId || item.parentId == toItem.id
        }
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

private fun List<TaskListItemDto>.getReorderedWhenMovingUp(
    fromItem: TaskListItemDto,
    toItem: TaskListItemDto
): List<TaskListItemDto> {
    return this
        .filterAndSortByOrder { item ->
            item.parentId == toItem.parentId
        }
        .toMutableList()
        .apply {
            val insertionIndex = indexOfFirst { it.id == toItem.id }

            if (fromItem.parentId == toItem.parentId) {
                add(insertionIndex, fromItem)
                removeAt(indexOfLast { it.id == fromItem.id })
            } else {
                add(insertionIndex, fromItem.copy(parentId = toItem.parentId))
            }
        }
        .mapIndexed { i, item -> item.copy(order = i) }
}

private fun List<TaskListItemDto>.getReorderedWhenMovingUpAsChild(
    fromItem: TaskListItemDto,
    toItem: TaskListItemDto
): List<TaskListItemDto> {
    var fromLevelOrder = 0
    var toLevelOrder = 1

    return this
        .filterAndSortByOrder { item ->
            item.parentId == fromItem.parentId || item.parentId == toItem.id
        }
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