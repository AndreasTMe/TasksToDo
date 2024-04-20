package com.andreast.taskstodo.application.persistence

import com.andreast.taskstodo.domain.TaskList

interface IDbContext : AutoCloseable {
    val taskLists: IRepository<TaskList, Int>
}