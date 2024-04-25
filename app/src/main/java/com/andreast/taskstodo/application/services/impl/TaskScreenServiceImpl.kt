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

    override suspend fun getTaskListWithItems(id: Long): TaskListDto {
        val taskListWithItems = repository.getTaskListWithItems(id)
        val taskListDto = TaskListMappers.entityToDto(taskListWithItems.taskList)

        if (taskListWithItems.taskListItems.isEmpty()) {
            return taskListDto
        }

        taskListDto.items = getTaskListItemsWithoutParent(taskListWithItems.taskListItems)

        val taskListItemsMap = mapTaskListItemsWithParent(taskListWithItems.taskListItems)

        populateTaskListItemsChildrenRecursive(taskListDto.items, taskListItemsMap)

        return taskListDto
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
        repository.updateTaskListItem(
            TaskListItemMappers.onlyIdAndTitleToEntity(id, title)
        )
    }

    override suspend fun updateTaskListItemParentId(id: Long, parentId: Long) {
        repository.updateTaskListItem(
            TaskListItemMappers.onlyIdAndParentIdToEntity(id, parentId)
        )
    }

    override suspend fun updateTaskListItemOrder(id: Long, order: Int) {
        repository.updateTaskListItem(
            TaskListItemMappers.onlyIdAndOrderToEntity(id, order)
        )
    }

    override suspend fun updateTaskListItemCompletedState(id: Long, isCompleted: Boolean) {
        repository.updateTaskListItem(
            TaskListItemMappers.onlyIdAndIsCompletedToEntity(id, isCompleted)
        )
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
        items: List<TaskListItemDto>,
        itemsMap: MutableMap<Long, MutableList<TaskListItemDto>>
    ) {
        for (item in items) {
            if (itemsMap.isEmpty()) {
                return
            }

            if (!itemsMap.containsKey(item.id)) {
                continue
            }

            item.children = itemsMap[item.id]!!
            itemsMap.remove(item.id)
        }

        if (items.isEmpty()) {
            return
        }

        for (item in items) {
            populateTaskListItemsChildrenRecursive(item.children, itemsMap)
        }
    }
}