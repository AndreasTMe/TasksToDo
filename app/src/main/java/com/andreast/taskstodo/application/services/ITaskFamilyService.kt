package com.andreast.taskstodo.application.services

import com.andreast.taskstodo.application.dto.TaskListItemDto

interface ITaskFamilyService {
    fun getParentAndDescendants(parentId: Long, items: List<TaskListItemDto>): List<TaskListItemDto>
    fun getParentAndDescendantsIds(parentId: Long, items: List<TaskListItemDto>): List<Long>
    fun getAncestors(id: Long, items: List<TaskListItemDto>): List<TaskListItemDto>
    fun getDescendants(id: Long, items: List<TaskListItemDto>): List<TaskListItemDto>
    fun getParent(parentId: Long?, items: List<TaskListItemDto>): TaskListItemDto?
    fun getSiblings(item: TaskListItemDto, items: List<TaskListItemDto>): List<TaskListItemDto>
}