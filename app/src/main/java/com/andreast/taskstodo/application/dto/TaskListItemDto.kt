package com.andreast.taskstodo.application.dto

enum class Level(val value: Int) {
    None(-1),
    Zero(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5);

    operator fun plus(other: Int): Level {
        return fromInt(this.value + other)
    }

    operator fun minus(other: Int): Level {
        return fromInt(this.value - other)
    }

    operator fun times(other: Int): Int {
        return this.value * other
    }

    private fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: None
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