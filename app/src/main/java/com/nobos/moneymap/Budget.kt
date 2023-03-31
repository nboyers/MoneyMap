package com.nobos.moneymap

// Budget.kt
data class Budget(
    val id: String,
    val userId: String,
    val income: Int,
    val foodExpense: Int,
    val gasExpense: Int,
    val entertainmentExpense: Int,
    val savings: Int
)
