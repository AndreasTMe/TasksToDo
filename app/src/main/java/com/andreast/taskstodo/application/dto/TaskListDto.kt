package com.andreast.taskstodo.application.dto

data class TaskListDto(
    var id: Long = 0,
    var title: String = "",
    var items: MutableList<TaskListItemDto> = mutableListOf()
) {
    fun getParentAndChildrenIds(parentId: Long): List<Long> {
        val parent = getItemByIdRecursive(parentId, items) ?: return listOf()

        return listOf(parentId) + getAllIdsRecursive(parent.children)
    }

    fun removeItem(taskListItem: TaskListItemDto) {
        if (items.removeIf { it.id == taskListItem.id }) {
            return
        }

        for (item in items) {
            if (tryRemoveItemRecursive(taskListItem.id, item)) {
                return
            }
        }
    }

    private fun getItemByIdRecursive(
        parentId: Long,
        items: List<TaskListItemDto>
    ): TaskListItemDto? {
        val parent = items.find { it.id == parentId }

        if (parent != null) {
            return parent
        }

        for (item in items) {
            return getItemByIdRecursive(parentId, item.children) ?: continue
        }

        return null
    }

    private fun getAllIdsRecursive(items: List<TaskListItemDto>): List<Long> {
        val itemsList = mutableListOf<Long>()

        for (item in items) {
            itemsList.add(item.id)

            if (item.children.isNotEmpty()) {
                itemsList.addAll(getAllIdsRecursive(item.children))
            }
        }

        return itemsList
    }

    private fun tryRemoveItemRecursive(childId: Long, parent: TaskListItemDto): Boolean {
        if (parent.children.removeIf { it.id == childId }) {
            return true
        }

        for (item in parent.children) {
            if (tryRemoveItemRecursive(childId, item)) {
                return true
            }
        }

        return false
    }
}