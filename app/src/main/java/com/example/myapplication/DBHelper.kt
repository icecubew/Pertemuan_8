package com.androidsurya.sqliteexample

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBhelper(private val mCtx: Context) {
    private val mDbHelper: DatabaseHelper
    private var mDb: SQLiteDatabase? = null

    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_EMPLOYEES_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEES_TABLE)
            onCreate(db)
        }
    }

    fun Reset() {
        mDbHelper.onUpgrade(mDb!!, 1, 1)
    }

    @Throws(SQLException::class)
    fun open(): DBhelper {
        mDb = mDbHelper.writableDatabase
        return this
    }

    fun close() {
        mDbHelper.close()
    }

    fun insertEmpDetails(employee: Employee) {
        val cv = ContentValues()
        cv.put(EMP_PHOTO, Utility.getBytes(employee.getBitmap()))
        cv.put(EMP_NAME, employee.getName())
        cv.put(EMP_AGE, employee.getAge())
        mDb!!.insert(EMPLOYEES_TABLE, null, cv)
    }

    @Throws(SQLException::class)
    fun retriveEmpDetails(): Employee? {
        val cur = mDb!!.query(
            true, EMPLOYEES_TABLE, arrayOf(
                EMP_PHOTO,
                EMP_NAME, EMP_AGE
            ), null, null, null, null, null, null
        )
        if (cur.moveToFirst()) {
            val blob = cur.getBlob(cur.getColumnIndex(EMP_PHOTO))
            val name = cur.getString(cur.getColumnIndex(EMP_NAME))
            val age = cur.getInt(cur.getColumnIndex(EMP_AGE))
            cur.close()
            return Employee(Utility.getPhoto(blob), name, age)
        }
        cur.close()
        return null
    }

    companion object {
        const val EMP_ID = "id"
        const val EMP_NAME = "name"
        const val EMP_AGE = "age"
        const val EMP_PHOTO = "photo"
        private const val DATABASE_NAME = "EmployessDB.db"
        private const val DATABASE_VERSION = 1
        private const val EMPLOYEES_TABLE = "Employees"
        private const val CREATE_EMPLOYEES_TABLE = ("create table "
                + EMPLOYEES_TABLE + " (" + EMP_ID
                + " integer primary key autoincrement, " + EMP_PHOTO
                + " blob not null, " + EMP_NAME + " text not null unique, "
                + EMP_AGE + " integer );")
    }

    init {
        mDbHelper = DatabaseHelper(mCtx)
    }
}