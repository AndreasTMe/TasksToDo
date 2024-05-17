package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskFamilyService
import com.andreast.taskstodo.application.utils.Level

class TaskFamilyService : ITaskFamilyService {
    override fun getParentAndDescendants(
        parentId: Long,
        items: List<TaskListItemDto>
    ): List<TaskListItemDto> {
        val parent = getParent(parentId, items) ?: return emptyList()
        return listOf(parent) + getDescendants(listOf(parent), items)
    }

    override fun getParentAndDescendantsIds(
        parentId: Long,
        items: List<TaskListItemDto>
    ): List<Long> {
        if (items.none { it.id == parentId }) {
            return emptyList()
        }
        return listOf(parentId) + getDescendantsIds(listOf(parentId), items)
    }

    override fun getAncestors(id: Long, items: List<TaskListItemDto>): List<TaskListItemDto> {
        val index = items.indexOfFirst { it.id == id }
        if (index == -1 || items[index].level == Level.Zero) {
            return emptyList()
        }

        val ancestors = mutableListOf<TaskListItemDto>()
        var parentLevel = items[index].level - 1
        for (i in index - 1 downTo 0) {
            if (items[i].level == parentLevel) {
                ancestors.add(items[i])
                parentLevel -= 1
            }

            if (parentLevel < 0) {
                break
            }
        }

        return ancestors
    }

    override fun getDescendants(id: Long, items: List<TaskListItemDto>): List<TaskListItemDto> {
        val item = items.firstOrNull { it.id == id } ?: return emptyList()
        return getDescendants(listOf(item), items)
    }

    override fun getParent(parentId: Long?, items: List<TaskListItemDto>): TaskListItemDto? {
        return items.firstOrNull { it.id == parentId }
    }

    override fun getSiblings(
        item: TaskListItemDto,
        items: List<TaskListItemDto>
    ): List<TaskListItemDto> {
        return items.filter { it.parentId == item.parentId && it.id != item.id }
    }

    private fun getDescendants(
        parents: List<TaskListItemDto>,
        items: List<TaskListItemDto>
    ): List<TaskListItemDto> {
        val descendants = items.filter { item ->
            item.parentId in parents.map { it.id }
        }
        if (descendants.isEmpty()) {
            return emptyList()
        }

        return descendants + getDescendants(descendants, items)
    }

    private fun getDescendantsIds(
        parents: List<Long>,
        items: List<TaskListItemDto>
    ): List<Long> {
        val descendantsIds = items
            .filter { item ->
                item.parentId in parents
            }
            .takeIf {
                it.isNotEmpty()
            }
            ?.map {
                it.id
            }
        if (descendantsIds.isNullOrEmpty()) {
            return emptyList()
        }

        return descendantsIds + getDescendantsIds(descendantsIds, items)
    }
}