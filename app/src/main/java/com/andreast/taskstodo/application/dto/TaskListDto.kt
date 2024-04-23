package com.andreast.taskstodo.application.dto

data class TaskListDto(
    var id: Long = 0,
    var title: String = "",
    var items: MutableList<TaskListItemDto> = mutableListOf()
) {
    fun tryUpdateChecked(indexTree: List<Int>): Boolean {
        if (indexTree.isEmpty()) {
            return items.isEmpty()
        }

        return tryUpdateChecked(indexTree, 0, items)
    }

    private fun tryUpdateChecked(
        indexTree: List<Int>,
        i: Int,
        taskListItems: MutableList<TaskListItemDto>
    ): Boolean {
        if (i >= indexTree.size || indexTree[i] >= taskListItems.size) {
            return false
        } else if (i == indexTree.size - 1) {
            taskListItems[i].isCompleted = true
            return true
        }

        return tryUpdateChecked(indexTree, i + 1, taskListItems[i].children)
    }
}