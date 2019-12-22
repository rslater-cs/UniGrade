package com.rysl.unigrade.learningTree

class Subject(private val key: Int, private val name: String): Learner {
    private val table = "subject"
    private var percentage = -2
    private val subID = arrayOf("subID", "modID")
    private val subTable = "link_sub_mod"
    private var workingPercentage: Double? = null

    override fun getParent(): Learner? {
        return null
    }

    override fun getName(): String {
        return name
    }

    override fun getPercentage(): Int {
        return percentage
    }

    override fun getType(): Boolean? {
        return null
    }

    override fun getKey(): Int {
        return key
    }

    override fun getAll(): String {
        return "$table: $key, $name"
    }

    override fun getSubTable(): String {
        return subTable
    }

    override fun getSubID(index: Int): String {
        return subID[index]
    }

    override fun getTable(): String {
        return table
    }

    override fun workingPercentage(): Double {
        return workingPercentage!!
    }

    override fun setWorkingPercentage(percentage: Double) {
        workingPercentage = percentage
    }
}