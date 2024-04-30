package com.andreast.taskstodo.presentation.screens

const val TASK_LIST_SCREEN_ROUTE_KEY = "taskListId"

sealed class Screen(val route: String) {
    data object TaskListsScreen : Screen(route = "task_lists_screen")
    data object TaskListScreen : Screen(
        route = "task_list_screen?id={$TASK_LIST_SCREEN_ROUTE_KEY}"
    ) {
        fun createRoute(taskListId: String): String {
            assert(taskListId.isNotEmpty())
            return route.replace("{$TASK_LIST_SCREEN_ROUTE_KEY}", taskListId)
        }
    }
}