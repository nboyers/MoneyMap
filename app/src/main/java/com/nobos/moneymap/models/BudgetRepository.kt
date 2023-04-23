package com.nobos.moneymap.models

import UserData
import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BudgetRepository {
    private val database = FirebaseDatabase.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    fun saveBudgetData(day: Int, month: Int, year: Int, income: Int, expenses: List<Long>, savings: Long) {
        // Get current user's ID
        val userId = mAuth.currentUser?.uid ?: return

        // Create a new UserData object with the data
        val userData = UserData(
            income = income,
            entertainmentExpense = expenses[0],
            foodExpense = expenses[1],
            gasExpense = expenses[2],
            savings = savings,
            timestamp = System.currentTimeMillis()
        )

        // Save the userData to the database
        saveUserData(userId, year, month, day, userData)
            .addOnSuccessListener {
                Log.d(TAG, "UserData saved successfully!")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error saving UserData: $it")
            }
    }

    fun saveUserData(userId: String, year: Int, month: Int, day: Int, userData: UserData): Task<Void> {
        val reference = database.reference.child("Budget").child(userId)
            .child("Years").child(year.toString())
            .child("Months").child(month.toString())
            .child("Days").child(day.toString())
            .child("UserKeys").push()
        return reference.setValue(userData)
    }
}
