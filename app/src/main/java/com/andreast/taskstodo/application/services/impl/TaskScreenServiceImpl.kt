package com.andreast.taskstodo.application.services.impl

import com.andreast.taskstodo.application.dto.TaskListDto
import com.andreast.taskstodo.application.dto.TaskListItemDto
import com.andreast.taskstodo.application.mappers.TaskListItemMappers
import com.andreast.taskstodo.application.mappers.TaskListMappers
import com.andreast.taskstodo.application.persistence.ITasksRepository
import com.andreast.taskstodo.application.services.ITaskScreenService
import com.andreast.taskstodo.domain.TaskListItem
import java.util.stream.Collectors
import java.util.stream.Stream
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

        val taskListItems = taskListWithItems.taskListItems.stream()
        taskListDto.items = getTaskListItemsWithoutParent(taskListItems)

        val taskListItemsMap = mapTaskListItemsWithParent(taskListItems)

        populateTaskListItemsChildrenRecursive(taskListDto.items, taskListItemsMap)

        return taskListDto
    }

    override suspend fun insertTaskList(taskList: TaskListDto) {
        repository.insertTaskList(
            TaskListMappers.dtoToEntity(taskList)
        )
    }

    private fun getTaskListItemsWithoutParent(items: Stream<TaskListItem>): MutableList<TaskListItemDto> {
        return items
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

    private fun mapTaskListItemsWithParent(items: Stream<TaskListItem>): MutableMap<Long, MutableList<TaskListItemDto>> {
        val taskListItemsMap = mutableMapOf<Long, MutableList<TaskListItemDto>>()
        items
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