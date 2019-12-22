package com.rysl.unigrade.learningTree

class Assignment(private val key: Int, private val name: String, private val percentage: Int,
                 private val type: Boolean, private val parent: Learner): Learner {
    private val table = "assignment"
    private val subID = arrayOf("asgID")
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
        return type
    }

    override fun getKey(): Int {
        return key
    }

    override fun getAll(): String {
        return "$table: $key, $name, $percentage, $type"
    }

    override fun getSubTable(): String? {
        return null
    }

    override fun getSubID(index: Int): String {
        return subID[index]
    }

    override fun getTable(): String {
        return table
    }

    override fun workingPercentage(): Double {
        return -1.0
    }

    override fun setWorkingPercentage(percentage: Double) {
        return
    }
}