package com.nobos.moneymap.viewModels

import UserData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.nobos.moneymap.models.BudgetRepository

class SummaryViewModel : ViewModel() {
    private val budgetRepository = BudgetRepository()

    fun saveUserData(userId: String, year: Int, month: Int, day: Int, userData: UserData): Task<Void> {
        return budgetRepository.saveUserData(userId, year, month, day, userData)
    }
}
