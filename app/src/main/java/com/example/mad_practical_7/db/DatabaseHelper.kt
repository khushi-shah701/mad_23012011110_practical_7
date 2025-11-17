package com.example.mad_practical_7.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mad_practical_7.Person

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "person_db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(PersonDbTable.CREATE_TABLE)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int,
    ) {
        db?.execSQL("DROP TABLE IF EXISTS " + PersonDbTable.TABLE_NAME)
        onCreate(db)
    }

    fun insertPerson(person: Person): Long {
        val db = this.writableDatabase
        val values = getValues(person)
        val id = db.insert(PersonDbTable.TABLE_NAME, null, values)
        db.close()
        return id
    }

    private fun getValues(person: Person): ContentValues {
        val values = ContentValues()
        values.put(PersonDbTable.COLUMN_ID, person.id)
        values.put(PersonDbTable.COLUMN_PERSON_NAME, person.name)
        values.put(PersonDbTable.COLUMN_PERSON_EMAIL_ID, person.email)
        values.put(PersonDbTable.COLUMN_PERSON_PHONE_NO, person.phone)
        values.put(PersonDbTable.COLUMN_PERSON_ADDRESS, person.address)
        return values
    }

    fun getPerson(id: String): Person? {
        val db = this.readableDatabase
        val cursor = db.query(
            PersonDbTable.TABLE_NAME,
            arrayOf(
                PersonDbTable.COLUMN_ID,
                PersonDbTable.COLUMN_PERSON_NAME,
                PersonDbTable.COLUMN_PERSON_EMAIL_ID,
                PersonDbTable.COLUMN_PERSON_PHONE_NO,
                PersonDbTable.COLUMN_PERSON_ADDRESS
            ),
            PersonDbTable.COLUMN_ID + "=?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val person = getPerson(cursor)
            cursor.close()
            return person
        }
        return null
    }

    private fun getPerson(cursor: Cursor): Person {
        return Person(
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_EMAIL_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_PHONE_NO)),
            cursor.getString(cursor.getColumnIndexOrThrow(PersonDbTable.COLUMN_PERSON_ADDRESS))
        )
    }

    val allPersons: ArrayList<Person>
        get() {
            val persons = ArrayList<Person>()
            val selectQuery = "SELECT * FROM " + PersonDbTable.TABLE_NAME
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    persons.add(getPerson(cursor))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return persons
        }

    val personsCount: Int
        get() {
            val countQuery = "SELECT * FROM " + PersonDbTable.TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }

    fun updatePerson(person: Person): Int {
        val db = this.writableDatabase
        val values = getValues(person)
        val result = db.update(
            PersonDbTable.TABLE_NAME, values, PersonDbTable.COLUMN_ID + " = ?",
            arrayOf(person.id)
        )
        db.close()
        return result
    }

    fun deletePerson(person: Person) {
        val db = this.writableDatabase
        db.delete(
            PersonDbTable.TABLE_NAME, PersonDbTable.COLUMN_ID + " = ?",
            arrayOf(person.id)
        )
        db.close()
    }
}