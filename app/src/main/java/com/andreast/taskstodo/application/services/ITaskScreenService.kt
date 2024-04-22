package com.andreast.taskstodo.application.services

import com.andreast.taskstodo.application.dto.TaskListDto

interface ITaskScreenService {
    suspend fun getAllTaskLists(): List<TaskListDto>
    suspend fun getTaskListWithItems(id: Long): TaskListDto
    suspend fun insertTaskList(taskList: TaskListDto)
}