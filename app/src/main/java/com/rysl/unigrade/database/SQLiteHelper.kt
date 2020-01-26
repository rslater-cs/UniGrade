package com.rysl.unigrade.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.rysl.unigrade.constants.*

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    private var database: SQLiteDatabase? = writableDatabase

    init {
        if(database == null){
            onUpgrade(database, 1, 1)
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE ${TABLE_NAMES[0]} (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL)")
        db?.execSQL("CREATE TABLE ${TABLE_NAMES[1]} (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, LINKID INTEGER FOREIGN KEY REFERENCES SUBJECT(ID))")
        db?.execSQL("CREATE TABLE ${TABLE_NAMES[2]} (ID INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL, PERCENTAGE INTEGER NOT NULL, TYPE BOOLEAN NOT NULL, RESULT INTEGER, DESCRIPTION TEXT, LINKID INTEGER FOREIGN KEY REFERENCES MODULE(ID))")
        createTutorial()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TABLE_NAMES.forEach{item ->
            db?.execSQL("DROP TABLE IF EXISTS $item;")
        }
    }

    private fun createTutorial(){
        VALUES.forEachIndexed { index, _ ->
            VALUES[index].forEachIndexed{valueIndex, valueDef->
                setData(START_TUTORIAL_QUERY+TABLE_NAMES[index]+MID_TUTORIAL_QUERY+valueDef[valueIndex]+END_QUERY)
            }
        }
    }

    fun setData(query: String){
        database?.execSQL(query, null)
    }

    fun getData(query: String): Cursor {
        return database!!.rawQuery(query, null)
    }

    fun getMaxKey(query: String): Int{
        val result = database!!.rawQuery(query, null)
        result.moveToNext()
        return result.getInt(0)
    }

    fun getKeys(query: String): ArrayList<Int>{
        val keys = ArrayList<Int>()
        val results = database!!.rawQuery(query, null)
        while(results.moveToNext()){
            keys.add(results.getInt(0))
        }
        return keys
    }
}