package com.nobos.moneymap.models

// Budget.kt
data class Budget(
    val userId: String = "",
    val income: Long = 0,
    val foodExpense: Long = 0,
    val gasExpense: Long = 0,
    val entertainmentExpense: Long = 0,
    val savings: Long = 0,
    val day: Long = 1,
    val month: Long = 0,
    val year: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)



