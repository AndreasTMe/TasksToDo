package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.application.utils.Level
import com.andreast.taskstodo.application.utils.mappers.TaskListItemMappers
import com.andreast.taskstodo.application.utils.mappers.TaskListMappers
import com.andreast.taskstodo.domain.TaskListItem
import java.util.stream.Collectors
import javax.inject.Inject

class TaskScreenService @Inject constructor(
    private val repository: ITasksRepository
) : ITaskScreenService {

    override suspend fun getAllTaskLists(): List<TaskListDto> {
        return repository.getTaskLists()
            .stream()
            .map { entity ->
                TaskListMappers.entityToDto(entity)
            }
            .collect(Collectors.toList())
    }

    override suspend fun getTaskListById(id: Long): TaskListDto {
        return TaskListMappers.entityToDto(repository.getTaskListById(id))
    }

    override suspend fun getTaskListItemsByListId(id: Long): List<TaskListItemDto> {
        val taskListItemList = repository.getTaskListItemsByListId(id)

        if (taskListItemList.isEmpty()) {
            return emptyList()
        }

        val taskListParentItems = getTaskListItemsWithoutParent(taskListItemList)
        val taskListChildrenMap = mapTaskListItemsWithParent(taskListItemList)

        populateTaskListItemsChildren(taskListParentItems, taskListChildrenMap)
        calculateCompletedPercentages(taskListParentItems)

        return taskListParentItems
    }

    override suspend fun upsertTaskList(taskList: TaskListDto): Long {
        return repository.upsertTaskList(
            TaskListMappers.dtoToEntity(taskList)
        )
    }

    override suspend fun upsertTaskListItem(taskListItem: TaskListItemDto): Long {
        return repository.upsertTaskListItem(
            TaskListItemMappers.dtoToEntity(taskListItem)
        )
    }

    override suspend fun updateTaskListItemTitle(id: Long, title: String) {
        repository.updateTaskListItems(TaskListItemMappers.onlyIdAndTitleToEntity(id, title))
    }

    override suspend fun updateTaskListItemParentIdAndOrder(items: List<TaskListItemDto>) {
        repository.updateTaskListItems(
            *items
                .map {
                    TaskListItemMappers.onlyIdParentIdAndOrderToEntity(
                        it.id,
                        it.parentId,
                        it.order
                    )
                }
                .toTypedArray()
        )
    }

    override suspend fun updateTaskListItemsCompletedState(items: List<TaskListItemDto>) {
        repository.updateTaskListItems(
            *items
                .map {
                    TaskListItemMappers.onlyIdAndIsCompletedToEntity(it.id, it.isCompleted)
                }
                .toTypedArray()
        )
    }

    override suspend fun deleteTaskListById(id: Long) {
        repository.deleteTaskListById(id)
    }

    override suspend fun deleteTaskListItemsByIds(ids: List<Long>) {
        repository.deleteTaskListItemsById(ids.distinct())
    }

    private fun getTaskListItemsWithoutParent(items: List<TaskListItem>): MutableList<TaskListItemDto> {
        return items
            .stream()
            .filter {
                it.parentId == null
            }
            .sorted { a, b ->
                a.order.compareTo(b.order)
            }
            .map {
                return@map TaskListItemMappers.entityToDto(it)
            }
            .collect(Collectors.toList())
    }

    private fun mapTaskListItemsWithParent(items: List<TaskListItem>): MutableMap<Long, MutableList<TaskListItemDto>> {
        val taskListItemsMap = mutableMapOf<Long, MutableList<TaskListItemDto>>()
        items
            .stream()
            .filter {
                it.parentId != null
            }
            .forEach {
                val parentId = it.parentId!!
                if (taskListItemsMap.containsKey(parentId)) {
                    taskListItemsMap[parentId]!!.add(TaskListItemMappers.entityToDto(it))
                } else {
                    taskListItemsMap[parentId] = mutableListOf(TaskListItemMappers.entityToDto(it))
                }
            }

        return taskListItemsMap
    }

    private fun populateTaskListItemsChildren(
        items: MutableList<TaskListItemDto>,
        itemsMap: MutableMap<Long, MutableList<TaskListItemDto>>
    ) {
        var i = 0
        while (i < items.size && itemsMap.isNotEmpty()) {
            val item = items[i]

            if (!itemsMap.containsKey(item.id)) {
                i++
                continue
            }

            items.addAll(
                i + 1,
                itemsMap[item.id]!!
                    .sortedBy {
                        it.order
                    }
                    .map {
                        it.copy(
                            level = item.level + 1,
                            parentLevel = item.level
                        )
                    }
            )
            itemsMap.remove(item.id)
            items[i] = item.copy(hasChildren = true)

            i++
        }
    }

    private fun calculateCompletedPercentages(items: MutableList<TaskListItemDto>) {
        var level = Level.max()
        while (level > Level.Zero) {
            val filteredItems = items
                .filter { it.level == level }
                .let { list ->
                    list.associate { item ->
                        item.parentId to list.filter { it.parentId == item.parentId }
                    }
                }

            for (i in 0..<items.size) {
                if (filteredItems.containsKey(items[i].id)) {
                    val children = filteredItems[items[i].id]!!
                    val completed = children.count { it.isCompleted }
                    val partiallyCompleted = children
                        .filter { !it.isCompleted && it.childrenCompletedPercentage >= 0.0f }
                        .sumOf { it.childrenCompletedPercentage }

                    items[i] = items[i].copy(
                        childrenCompletedPercentage = (completed + partiallyCompleted) / children.size
                    )
                }
            }

            level -= 1
        }
    }
}

private fun <E> List<E>.sumOf(predicate: (E) -> Float): Float {
    var sum: Float = 0.0f
    for (element in this) {
        sum += predicate(element)
    }
    return sum
}
