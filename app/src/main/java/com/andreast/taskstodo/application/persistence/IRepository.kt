package com.andreast.taskstodo.application.persistence

interface IRepository<TEntity> {
    suspend fun getAll(): List<TEntity>
    suspend fun create(entity: TEntity): TEntity
    suspend fun update(entity: TEntity): TEntity
    suspend fun delete(id: Long): Boolean
}