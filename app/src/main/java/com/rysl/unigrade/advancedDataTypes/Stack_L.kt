package com.rysl.unigrade.advancedDataTypes

import com.rysl.unigrade.learningTree.Learner
import java.util.*
import kotlin.collections.ArrayList

class Stack_L {
    private var learnerStack = ArrayList<Learner>()
    private var sizes = Stack<Int>()

    fun push(learners: ArrayList<Learner>){
        learners.forEach{ learner ->
            learnerStack.add(learner)
        }
        sizes.push(learners.size)
    }

    fun pop(): ArrayList<Learner>{
        val tempArray = ArrayList<Learner>()
        tempArray.addAll(learnerStack.dropLast(sizes.pop()))
        return tempArray
    }
}