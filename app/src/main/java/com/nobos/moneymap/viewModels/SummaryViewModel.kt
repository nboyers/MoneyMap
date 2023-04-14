package com.nobos.moneymap.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.nobos.moneymap.models.Budget
import com.nobos.moneymap.models.BudgetRepository

class SummaryViewModel : ViewModel() {
    private val budgetRepository = BudgetRepository()

    val monthlyExpenses: MutableLiveData<List<Budget>> = MutableLiveData()

    init {
        // Initialize your data here
        // You can call fetchBudgetsForMonth or use another method to fetch and set the monthlyExpenses LiveData value
    }

    fun fetchBudgetsForMonth(
        userId: String,
        monthStart: Long,
        monthEnd: Long,
        onDataFetched: (List<Budget>) -> Unit,
        onError: (DatabaseError) -> Unit
    ): ValueEventListener {
        return budgetRepository.fetchBudgetsForMonth(userId, monthStart, monthEnd, onDataFetched, onError)
    }

    fun saveBudget(budget: Budget): Task<Void> {
        return budgetRepository.saveBudget(budget)
    }
}
