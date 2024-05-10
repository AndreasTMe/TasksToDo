package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.services.ITaskFamilyService

class TaskFamilyService : ITaskFamilyService {
    override fun getParentAndChildren(
        parentId: Long,
        items: List<TaskListItemDto>
    ): List<TaskListItemDto> {
        val parent = items.firstOrNull { it.id == parentId } ?: return emptyList()
        return listOf(parent) + getChildren(listOf(parent), items)
    }

    override fun getParentAndChildrenIds(
        parentId: Long,
        items: List<TaskListItemDto>
    ): List<Long> {
        if (items.none { it.id == parentId }) {
            return emptyList()
        }
        return listOf(parentId) + getChildrenIds(listOf(parentId), items)
    }

    private fun getChildren(
        parents: List<TaskListItemDto>,
        items: List<TaskListItemDto>
    ): List<TaskListItemDto> {
        val children = items.filter { item ->
            item.parentId in parents.map { it.id }
        }
        if (children.isEmpty()) {
            return emptyList()
        }

        return children + getChildren(children, items)
    }

    private fun getChildrenIds(
        parents: List<Long>,
        items: List<TaskListItemDto>
    ): List<Long> {
        val childrenIds = items
            .filter { item ->
                item.parentId in parents
            }
            .takeIf {
                it.isNotEmpty()
            }
            ?.map {
                it.id
            }
        if (childrenIds.isNullOrEmpty()) {
            return emptyList()
        }

        return childrenIds + getChildrenIds(childrenIds, items)
    }
}