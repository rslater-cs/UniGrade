package com.rysl.unigrade.learningTree

interface Learner {
    fun getParent(): Learner?

    fun getName(): String

    fun getPercentage(): Int

    fun getType(): Boolean?

    fun getKey(): Int

    fun getAll(): String

    fun getSubTable(): String

    fun getSubID(index: Int): String

    fun getTable(): String

    fun workingPercentage(): Double

    fun setWorkingPercentage(percentage: Double)

}