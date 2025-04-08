package com.example.upkeep.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception

class UserHistoryDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        createTable(db)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_HISTORY")
        createTable(db)
    }

    private fun createTable(db: SQLiteDatabase){
        val CREATE_TABLE_USER_HISTORY = """
            CREATE TABLE $TABLE_USER_HISTORY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                password TEXT,
                firstName TEXT,
                lastName TEXT,
                favoriteColor TEXT
            );
        """
        db.execSQL(CREATE_TABLE_USER_HISTORY)
    }


    fun updateFirstName(
        username: String,
        newFirstName: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("firstName", newFirstName)
        }

        val whereClause = "username = ?"
        val whereArgs = arrayOf(username)

        return db.update(TABLE_USER_HISTORY, values, whereClause, whereArgs)
    }


    fun updateLastName(
        username: String,
        newLastName: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("lastName", newLastName)
        }

        val whereClause = "username = ?"
        val whereArgs = arrayOf(username)

        return db.update(TABLE_USER_HISTORY, values, whereClause, whereArgs)
    }


    fun updateFavoriteColor(
        username: String,
        newFavoriteColor: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("favoriteColor", newFavoriteColor)
        }

        val whereClause = "username = ?"
        val whereArgs = arrayOf(username)

        return db.update(TABLE_USER_HISTORY, values, whereClause, whereArgs)
    }


    fun insertUserHistory(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        return db.insert(TABLE_USER_HISTORY, null, values)
    }


    fun getAllUserHistories(): List<UserHistory> {
        val userHistories = mutableListOf<UserHistory>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER_HISTORY", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val username = cursor.getString(1)
            val password = cursor.getString(2)
            val firstName = cursor.getString(3)
            val lastName = cursor.getString(4)
            val favoriteColor = cursor.getString(5)

            userHistories.add(UserHistory(id, username, password, firstName, lastName, favoriteColor))
            Log.d("UserHistory", "ID: $id, Username: $username, Password: $password")
        }
        cursor.close()
        return userHistories
    }

    fun getUserCredentials(username: String): List<UserHistory> {
        val userCredentials = mutableListOf<UserHistory>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                "SELECT * FROM $TABLE_USER_HISTORY WHERE username = ?",
                arrayOf(username)
            )

            if (cursor.moveToFirst()) {
                val userCredential = UserHistory(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName")),
                    lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName")),
                    favoriteColor = cursor.getString(cursor.getColumnIndexOrThrow("favoriteColor"))
                )
                userCredentials.add(userCredential)
            }
        } catch (e: Exception) {

            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return userCredentials
    }


    fun deleteUserHistory(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_USER_HISTORY, "id = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATABASE_NAME = "user_history.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_USER_HISTORY = "user_history"
    }
}


data class UserHistory(
    val id: Int,
    val username: String,
    val password: String,
    val firstName: String?,
    val lastName: String?,
    val favoriteColor: String?
)