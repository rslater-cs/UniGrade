package com.rysl.unigrade.database

import com.rysl.unigrade.learningTree.Learner

class SQLAcess(private val database: SQLiteHelper) {

    //methods for getting data in database

    fun getLearner(parent: Learner?, table: String): ArrayList<Learner>{
        val query = "SELECT $table.* FROM " + when(parent){
            null -> table
            else -> parent
        }
    }
}