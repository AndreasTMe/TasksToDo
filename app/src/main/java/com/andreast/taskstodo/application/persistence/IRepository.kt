package com.andreast.taskstodo.application.persistence

interface IRepository<TEntity, TId : Comparable<TId>> {
    suspend fun getAll(): List<TEntity>
    suspend fun create(item: TEntity): TEntity
    suspend fun update(item: TEntity): TEntity
    suspend fun delete(id: TId): Boolean
}