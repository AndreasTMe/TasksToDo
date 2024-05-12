package com.andreast.taskstodo.application.dto

enum class Level(private val value: Int) {
    None(-1),
    Zero(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5);

    companion object {
        fun addValues(l1: Level, l2: Level): Int {
            return l1.value + l2.value
        }

        fun maxValue(): Int {
            return entries.maxOf { it.value }
        }
    }

    operator fun plus(other: Int): Level {
        return entries.firstOrNull { it.value == value + other } ?: None
    }

    operator fun times(other: Int): Int {
        return this.value * other
    }
}

data class TaskListItemDto(
    val id: Long = 0,
    val parentId: Long? = null,
    val parentLevel: Level = Level.Zero,
    val taskListId: Long = 0,
    val title: String = "",
    val level: Level = Level.Zero,
    val order: Int = 0,
    val isCompleted: Boolean = false
)