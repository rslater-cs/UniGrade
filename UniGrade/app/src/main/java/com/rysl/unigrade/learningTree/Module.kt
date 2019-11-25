package com.rysl.unigrade.learningTree

class Module(private val key: Int, private val name: String, private val parent: Learner): Learner {
    private val table = "module"
    private val subTable = "link_mod_asg"
    private val subID = arrayOf("modID", "asgID")
    private var workingPercentage: Double? = null
    private val percentage = -2

    override fun getParent(): Learner? {
        return parent
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