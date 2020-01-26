package com.rysl.unigrade.database

import android.database.Cursor
import com.rysl.unigrade.learningTree.Assignment
import com.rysl.unigrade.learningTree.Learner
import com.rysl.unigrade.learningTree.Module
import com.rysl.unigrade.learningTree.Subject

class SQLAccess(private val database: SQLiteHelper) {

    //methods for getting data in database

    fun getLearner(parent: Learner?, table: String): ArrayList<Learner>{
        val query = "SELECT $table.* FROM $table" + when(parent){
            null -> ";"
            else -> {" WHERE LINKID = ${parent.getKey()};"}
        }

        val results = database.getData(query)

        val learners = ArrayList<Learner>()

        return when(table){
            "subject" -> packSubjects(results)
            "module" -> packModules(results, parent!!)
            else -> packAssignments(results, parent!!)
        }
    }

    fun packSubjects(results: Cursor): ArrayList<Learner>{
        val subjects = ArrayList<Learner>()
        while(results.moveToNext()){
            subjects.add(Subject(results.getInt(0), results.getString(1)))
        }
        return subjects
    }

    fun packModules(results: Cursor, parent: Learner): ArrayList<Learner>{
        val modules = ArrayList<Learner>()
        while(results.moveToNext()){
            modules.add(Module(results.getInt(0), results.getString(1), parent))
        }
        return modules
    }

    fun packAssignments(results: Cursor, parent: Learner): ArrayList<Learner>{
        val assignments = ArrayList<Learner>()
        while(results.moveToNext()){
            assignments.add(Assignment(results.getInt(0), results.getString(1), results.getInt(2), (results.getInt(3) > 0), parent))
        }
        return assignments
    }

    fun getResult(learner: Learner): Int{
        val query = "SELECT RESULT FROM ASSIGNMENT WHERE ID = ${learner.getKey()};"
        val results = database.getData(query)
        results.moveToNext()
        return results.getInt(0)
    }

    fun getDescription(learner: Learner): String{
        val query = "SELECT DESCRIPTION FROM ASSIGNMENT WHERE ID = ${learner.getKey()};"
        val results = database.getData(query)
        results.moveToNext()
        return results.getString(0)
    }

    fun getWorkingPercent(learner: Learner, percentages: java.util.ArrayList<Double>): java.util.ArrayList<Double> {
        var percentages = percentages
        var table = "end"
        when (learner.getTable()) {
            "subject" -> table = "module"
            "module" -> table = "assignment"
        }
        if (table !== "end") {
            val learners = getLearner(learner, table)
            for (x in learners.indices) {
                println(x)
                percentages = getWorkingPercent(learners[x], percentages)
            }
            println(percentages)
            return percentages
        }
        val result = database.getData("SELECT RESULT FROM assignment WHERE ID = " +
                learner.getKey().toString() + ";")
        result.moveToNext()
        val percent = result.getInt(0).toDouble()
        val percentage = learner.getPercentage().toDouble()
        if (percent != -1.0) {
            percentages.add(percent)
        } else {
            percentages.add(0.0)
        }
        percentages.add(percentage)
        return percentages
    }

    fun getPredictedPercentage(learner: Learner): Double {
        val percentages = getWorkingPercent(learner, java.util.ArrayList())
        var x = 0
        var totalPercent = 0.0
        var totalWeight = 0.0
        while (x < percentages.size) {
            println(x)
            if (percentages[x] == 0.0) {
                percentages.removeAt(x)
                percentages.removeAt(x)
            } else {
                totalPercent += percentages[x] * (percentages[x + 1] / 100)
                totalWeight += percentages[x + 1]
                x += 2
            }
        }
        return Math.round(totalPercent * 100 / totalWeight * 10).toDouble() / 10
    }

    //Methods for setting data in database

    fun setLearner(parent: Learner?, name: String, percentage: Int, type: Boolean){
        var query = "";
        if(parent == null){
            query = this.subjectInsertQuery(name)
        } else if(parent.getTable() == "subject"){
            query = this.moduleInsertQuery(name, parent.getKey())
        } else{
            query = this.assignmentInsertQuery(name, percentage, type, parent.getKey())
        }
        database.setData(query)
    }

    private fun subjectInsertQuery(name: String): String{
        return "INSERT INTO SUBJECT (NAME) VALUES (\"$name\");"
    }

    private fun moduleInsertQuery(name: String, key: Int): String{
        return "INSERT INTO MODULE (NAME, LINKID) VALUES (\"$name\", $key);"
    }

    private fun assignmentInsertQuery(name: String, percentage: Int, type: Boolean, key: Int): String{
        return "INSERT INTO ASSIGNMENT (NAME, PERCENTAGE, TYPE, RESULT, LINKID) VALUES (\"$name\", $percentage, ${this.toBinary(type)}, -1, $key);"
    }

    private fun toBinary(type: Boolean): Int{
        return when(type){
            true -> 1
            else -> 0
        }
    }

    fun setDescription(learner: Learner, description: String){
        database.setData("UPDATE ASSIGNMENT SET DESCRIPTION = \"$description\" WHERE ID = ${learner.getKey()};")
    }

    fun setResult(learner: Learner, result: Int){
        database.setData("UPDATE ASSIGNMENT SET RESULT = $result WHERE ID = ${learner.getKey()};")
    }

    //Methods for deleting data in database

    fun deleteLearner(key: Int, table: String){
        val connTable = when(table){
            "subject" -> "module"
            "module" -> "assignment"
            else -> "end"
        }

        if(connTable != "end") {
            val keys = database.getData("SELECT ID FROM $connTable WHERE LINKID = $key;")
            while(keys.moveToNext()){
                deleteLearner(keys.getInt(0), connTable)
            }
        }
        database.setData("DELETE FROM $table WHERE ID = $key")
    }

    //Methods for editing data in database

    fun editLearner(learner: Learner, name: String, percentage: Int, type: Boolean){
        var query = "UPDATE ${learner.getTable()} SET NAME = \"$name\""
        if(learner.getTable() == "assignment"){
            query += ", PERCENTAGE = $percentage, TYPE = ${this.toBinary(type)}"
        }
        query += "WHERE ID = ${learner.getKey()};"
        database.setData(query)
    }
}