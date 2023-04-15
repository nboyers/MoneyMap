package com.nobos.moneymap.models

// Budget.kt
data class Budget(
    val userId: String = "",
    val income: Int = 0,
    val foodExpense: Int = 0,
    val gasExpense: Int = 0,
    val entertainmentExpense: Int = 0,
    val savings: Int = 0,
    val day: Int = 1,
    val month: Int = 0,
    val year: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)



