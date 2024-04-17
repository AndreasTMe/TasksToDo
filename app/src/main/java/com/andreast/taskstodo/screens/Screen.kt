package com.andreast.taskstodo.screens

const val TASK_ITEM_SCREEN_ROUTE_KEY = "taskId"

sealed class Screen(val route: String) {
    data object TaskScreen : Screen(route = "task_screen")
    data object TaskItemScreen : Screen(
        route = "task_item_screen?id={$TASK_ITEM_SCREEN_ROUTE_KEY}"
    ) {
        fun createRoute(taskId: String = ""): String {
            if (taskId.isNotEmpty()) {
                return route.replace("{$TASK_ITEM_SCREEN_ROUTE_KEY}", taskId)
            }

            return route.split("?")[0]
        }
    }
}