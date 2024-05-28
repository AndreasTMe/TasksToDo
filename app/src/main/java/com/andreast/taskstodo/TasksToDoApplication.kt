package com.andreast.taskstodo

import android.app.Application
import com.andreast.taskstodo.domain.AppDirectory
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class TasksToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val externalFilesDir = getExternalFilesDir(null) ?: return

        AppDirectory.getAll()
            .forEach { directory ->
                File(externalFilesDir, directory.path)
                    .takeIf {
                        !it.exists()
                    }?.mkdir()
            }
    }
}