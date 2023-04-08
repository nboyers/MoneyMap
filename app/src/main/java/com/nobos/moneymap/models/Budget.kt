package com.nobos.moneymap.models

// Budget.kt
data class Budget(
    val userId: String = "",
    val income: Int = 0,
    val foodExpense: Int = 0,
    val gasExpense: Int = 0,
    val entertainmentExpense: Int = 0,
    val savings: Int = 0,
    val periodType: String = "weekly", // weekly, monthly, or yearly
    val timestamp: Long = System.currentTimeMillis() // Timestamp when the data was added
)
