package com.nobos.moneymap.models

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BudgetRepository {
    private val database = FirebaseDatabase.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val budgetRef = database.getReference("Budget")


    fun fetchBudgetsForMonth(
        userId: String,
        monthStart: Long,
        monthEnd: Long,
        onDataFetched: (List<Budget>) -> Unit,
        onError: (DatabaseError) -> Unit
    ): ValueEventListener {
        val database = FirebaseDatabase.getInstance()
        val budgetRef = database.getReference("Budget").child(userId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val budgets = snapshot.children.mapNotNull { it.getValue(Budget::class.java) }
                onDataFetched(budgets)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        }

        budgetRef.orderByChild("timestamp")
            .startAt(monthStart.toDouble())
            .endAt(monthEnd.toDouble())
            .addListenerForSingleValueEvent(valueEventListener)

        return valueEventListener
    }

    fun saveBudget(budget: Budget): Task<Void> {
        val newBudgetRef = budgetRef.child(mAuth.currentUser?.uid ?: "")
        return newBudgetRef.setValue(budget)
    }
}
