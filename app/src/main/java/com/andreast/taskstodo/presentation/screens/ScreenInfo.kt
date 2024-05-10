package com.andreast.taskstodo.presentation.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenInfo() {
    @Serializable
    data object TaskListsScreen : ScreenInfo()

    @Serializable
    data class TaskListScreen(
        val id: Long
    ) : ScreenInfo()
}