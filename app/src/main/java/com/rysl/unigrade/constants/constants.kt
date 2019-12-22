package com.rysl.unigrade.constants

val TABLE_NAMES = arrayOf("subject", "link_sub_mod", "module", "link_mod_asg", "assignment")
val TABLE_STATES = arrayOf("subject", "module", "assignment", "summary", "end")

const val DATABASE_NAME = "unigradekotdb"
const val START_TUTORIAL_QUERY = "INSERT INTO "
const val MID_TUTORIAL_QUERY = " VALUES ("
const val END_QUERY = ");"
val SUBJECT_VALUES = arrayOf("1, \"Tap card to see modules\"")
val MOD_LINK_VALUES = arrayOf("1,1", "1,2", "1,3", "1,4")
val MODULE_VALUES = arrayOf("1, \"This is a module\"", "2, \"To delete card, swipe it\"",
        "3, \"To edit a card, hold it\"", "4, \"Tap card to see assignments\"")
val ASG_LINK_VALUES = arrayOf("4,1", "4,2", "4,3",
        "4,4")
val ASSIGNMENT_VALUES = arrayOf("1, \"This is an assignment\", 50, 0, -1, null",
        "2, \"This is a test\", 20, 1, -1, null", "3, \"This is a coursework\", 30, 0, -1, null",
        "4, \"Click '+' to add a card\", 0, 0, -1, null")
val VALUES = arrayOf(SUBJECT_VALUES, MOD_LINK_VALUES, MODULE_VALUES, ASG_LINK_VALUES, ASSIGNMENT_VALUES)