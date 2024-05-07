package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.application.utils.mappers.TaskListItemMappers
import com.andreast.taskstodo.application.utils.mappers.TaskListMappers
import com.andreast.taskstodo.domain.TaskListItem
import java.util.stream.Collectors
import javax.inject.Inject

class TaskScreenServiceImpl @Inject constructor(
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
            return listOf()
        }

        val taskListParentItems = getTaskListItemsWithoutParent(taskListItemList)
        val taskListChildrenMap = mapTaskListItemsWithParent(taskListItemList)

        populateTaskListItemsChildrenRecursive(taskListParentItems, taskListChildrenMap)

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

    override suspend fun updateTaskListItemParentId(id: Long, parentId: Long) {
        repository.updateTaskListItems(TaskListItemMappers.onlyIdAndParentIdToEntity(id, parentId))
    }

    override suspend fun updateTaskListItemOrder(id: Long, order: Int) {
        repository.updateTaskListItems(TaskListItemMappers.onlyIdAndOrderToEntity(id, order))
    }

    override suspend fun updateTaskListItemsCompletedState(items: List<Pair<Long, Boolean>>) {
        repository.updateTaskListItems(
            *items
                .map {
                    TaskListItemMappers.onlyIdAndIsCompletedToEntity(it.first, it.second)
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

    private fun populateTaskListItemsChildrenRecursive(
        items: MutableList<TaskListItemDto>,
        itemsMap: MutableMap<Long, MutableList<TaskListItemDto>>
    ) {
        var index = 0
        while (index < items.size || itemsMap.isNotEmpty()) {
            val itemId = items[index].id

            if (!itemsMap.containsKey(itemId)) {
                index++
                continue
            }

            items.addAll(
                index + 1,
                itemsMap[itemId]!!
                    .sortedBy {
                        it.order
                    }
                    .map {
                        it.copy(
                            level = items[index].level + 1,
                            parentLevel = items[index].level
                        )
                    }
            )
            itemsMap.remove(itemId)

            index++
        }
    }
}