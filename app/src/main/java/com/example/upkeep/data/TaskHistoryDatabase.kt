package com.example.upkeep.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.database.Cursor
import java.lang.Exception

class TaskHistoryDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        createTable(db)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASK_HISTORY")
        createTable(db)
    }

    private fun createTable(db: SQLiteDatabase){
        val CREATE_TABLE_TASK_HISTORY = """
            CREATE TABLE $TABLE_TASK_HISTORY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                taskName TEXT,
                taskFrequency INTEGER,
                timestamp DATETIME
            );
        """
        db.execSQL(CREATE_TABLE_TASK_HISTORY)
    }


    fun insertTaskHistory(taskName: String, taskFrequency: Int, currentTime: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("taskName", taskName)
            put("taskFrequency", taskFrequency)
            put("timestamp", currentTime)
        }
        return db.insert(TABLE_TASK_HISTORY, null, values)
    }


    fun getAllTaskHistories(): List<TaskHistory> {
        val taskHistories = mutableListOf<TaskHistory>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASK_HISTORY ORDER BY timestamp desc", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val taskName = cursor.getString(1)
            val taskFrequency = cursor.getInt(2)
            val timestamp = cursor.getString(3)

            taskHistories.add(TaskHistory(id, taskName, taskFrequency, timestamp))
            Log.d("TaskHistory", "ID: $id, Name: $taskName, Frequency: $taskFrequency, Timestamp: $timestamp")
        }
        cursor.close()
        return taskHistories
    }

    fun getFirstRow(taskType: String): List<TaskHistory> {
        val taskHistories = mutableListOf<TaskHistory>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                "SELECT * FROM $TABLE_TASK_HISTORY WHERE taskName = ? ORDER BY timestamp DESC LIMIT 1",
                arrayOf(taskType)
            )

            if (cursor.moveToFirst()) {
                val taskHistory = TaskHistory(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    taskName = cursor.getString(cursor.getColumnIndexOrThrow("taskName")),
                    taskFrequency = cursor.getInt(cursor.getColumnIndexOrThrow("taskFrequency")),
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                )
                taskHistories.add(taskHistory)
            }
        } catch (e: Exception) {

            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return taskHistories
    }


    fun deleteTaskHistory(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_TASK_HISTORY, "id = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATABASE_NAME = "task_history.db"
        private const val DATABASE_VERSION = 9
        private const val TABLE_TASK_HISTORY = "task_history"
    }
}


data class TaskHistory(
    val id: Int,
    val taskName: String,
    val taskFrequency: Int,
    val timestamp: String
)