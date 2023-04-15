package com.nobos.moneymap.viewModels

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.nobos.moneymap.models.Budget
import com.nobos.moneymap.models.BudgetRepository

class SummaryViewModel : ViewModel() {
    private val budgetRepository = BudgetRepository()


    fun saveBudget(budget: Budget): Task<Void> {
        return budgetRepository.saveBudget(budget)
    }
}
