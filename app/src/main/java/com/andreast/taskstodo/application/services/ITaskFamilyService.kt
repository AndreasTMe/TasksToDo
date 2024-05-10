package com.andreast.taskstodo.application.services

import com.andreast.taskstodo.application.dto.TaskListItemDto

interface ITaskFamilyService {
    fun getParentAndChildren(parentId: Long, items: List<TaskListItemDto>): List<TaskListItemDto>
    fun getParentAndChildrenIds(parentId: Long, items: List<TaskListItemDto>): List<Long>
}