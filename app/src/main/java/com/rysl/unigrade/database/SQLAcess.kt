package com.rysl.unigrade.database

import com.rysl.unigrade.learningTree.Assignment
import com.rysl.unigrade.learningTree.Learner
import com.rysl.unigrade.learningTree.Module
import com.rysl.unigrade.learningTree.Subject

class SQLAcess(private val database: SQLiteHelper) {

    //methods for getting data in database

    fun getLearner(parent: Learner?, table: String): ArrayList<Learner>{
        val query = "SELECT $table.* FROM " + when(parent){
            null -> "$table;"
            else -> {"${parent.getTable()} JOIN ${parent.getSubTable()} ON ${parent.getTable()}.ID = " +
                    "${parent.getSubTable()}.${parent.getSubID(0)} JOIN $table ON " +
                    "${parent.getSubTable()}.${parent.getSubID(1)}=$table.ID WHERE " +
                    "${parent.getTable()}.ID=${parent.getKey()};"}
        }
        val results = database.getData(query)

        val learners = ArrayList<Learner>()

        while(results.moveToNext()){
            learners.add(when(table){
                "subject" -> Subject(results.getInt(0), results.getString(1))
                "module" -> Module(results.getInt(0), results.getString(1), parent!!)
                else -> Assignment(results.getInt(0), results.getString(1), results.getInt(2), (results.getInt(3) > 0), parent!!)
            })
        }
        return learners
    }
}