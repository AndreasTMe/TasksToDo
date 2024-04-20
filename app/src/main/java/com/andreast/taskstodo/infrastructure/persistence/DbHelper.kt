package com.andreast.taskstodo.infrastructure.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.andreast.taskstodo.infrastructure.persistence.tables.TaskList

class DbHelper(context: Context, factory: SQLiteDatabase.CursorFactory? = null) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TASKS_DATABASE"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS ${TaskList.Table.NAME} (" +
                    "${TaskList.Table.COLUMN_ID} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "${TaskList.Table.COLUMN_TITLE} TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // if (oldVersion < 2) {
        //     // Add alterations
        // }
    }
}
