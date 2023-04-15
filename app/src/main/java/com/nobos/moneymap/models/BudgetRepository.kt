package com.nobos.moneymap.models

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BudgetRepository {
    private val database = FirebaseDatabase.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val budgetRef = database.getReference("Budget")

    fun saveBudget(budget: Budget): Task<Void> {
        val newBudgetRef = budgetRef.child(mAuth.currentUser?.uid ?: "")
        return newBudgetRef.setValue(budget)
    }
}
