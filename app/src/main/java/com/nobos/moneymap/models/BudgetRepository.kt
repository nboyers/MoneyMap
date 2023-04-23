package com.nobos.moneymap.models

import UserData
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase


class BudgetRepository {
    private val database = FirebaseDatabase.getInstance()

    fun saveUserData(userId: String, year: Int, month: Int, day: Int, userData: UserData): Task<Void> {
        val reference = database.reference.child("Budget")
            .child(userId)
            .child("year").child(year.toString())
            .child("month").child(month.toString())
            .child("day").child(day.toString())
            .push()
        return reference.setValue(userData)
    }
}

