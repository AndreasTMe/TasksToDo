package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.application.utils.Level
import com.andreast.taskstodo.application.utils.mappers.TaskListItemMappers
import com.andreast.taskstodo.application.utils.mappers.TaskListMappers
import com.andreast.taskstodo.domain.TaskListItem
import java.util.Stack
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
        hideItemsBasedOnExpandedState(taskListParentItems)

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

    override suspend fun updateTaskListItemExpandedState(item: TaskListItemDto) {
        repository.updateTaskListItems(
            TaskListItemMappers.onlyIdAndIsExpandedToEntity(item.id, item.isExpanded)
        )
    }

    override suspend fun updateTaskListItemsExpandedState(items: List<TaskListItemDto>) {
        repository.updateTaskListItems(
            *items
                .map {
                    TaskListItemMappers.onlyIdAndIsExpandedToEntity(it.id, it.isExpanded)
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
                return@map TaskListItemMappers.entityToDto(it, false)
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

                    items[i] = items[i].copy(
                        completedPercentage = children.sumOf { it.completedPercentage } / children.size
                    )
                }
            }

            level -= 1
        }
    }

    private fun hideItemsBasedOnExpandedState(items: MutableList<TaskListItemDto>) {
        val state = Stack<Pair<Level, Boolean>>()
        state.push(Pair(items[0].level, items[0].isExpanded))

        var i = 1
        while (i < items.size) {
            if (items[i].level > state.peek().first) {
                if (state.peek().second && !items[i].isExpanded) {
                    items[i] = items[i].copy(isHidden = false)

                    if (items[i].hasChildren) {
                        state.push(Pair(items[i].level, items[i].isExpanded))
                    }
                } else {
                    items[i] = items[i].copy(isHidden = !state.peek().second)
                }
            } else {
                if (state.isNotEmpty()) {
                    state.pop()
                    if (state.isNotEmpty()) {
                        items[i] = items[i].copy(isHidden = !state.peek().second)
                    }
                }
                state.push(Pair(items[i].level, items[i].isExpanded))
            }

            i++
        }
    }
}