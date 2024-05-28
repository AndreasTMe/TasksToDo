package com.andreast.taskstodo.domain

import java.io.File

sealed class AppDirectory(vararg names: String = arrayOf()) {

    val path = when (names.size) {
        0 -> ""
        1 -> names[0]
        else -> names.joinToString(separator = File.pathSeparator)
    }

    companion object {
        fun getAll(): List<AppDirectory> {
            return listOf(
                Root,
                Tasks
            )
        }
    }

    data object Root : AppDirectory()
    data object Tasks : AppDirectory("tasks")
}

