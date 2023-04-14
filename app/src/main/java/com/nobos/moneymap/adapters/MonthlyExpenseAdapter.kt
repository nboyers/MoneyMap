package com.nobos.moneymap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nobos.moneymap.R
import com.nobos.moneymap.models.Budget
import java.text.SimpleDateFormat
import java.util.*

class MonthlyExpenseAdapter(
    private var monthlyExpenses: List<Budget>,
    private val onItemClicked: (Budget) -> Unit
) : RecyclerView.Adapter<MonthlyExpenseAdapter.MonthlyExpenseViewHolder>() {

    inner class MonthlyExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthTextView: TextView = itemView.findViewById(R.id.monthRecyclerView)
        // Add other TextViews

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(monthlyExpenses[position])
                }
            }
        }
    }

    fun updateMonthlyExpenses(newExpenses: List<Budget>) {
        val diffCallback = MonthlyExpenseDiffCallback(monthlyExpenses, newExpenses)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        monthlyExpenses = newExpenses
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monthly_expense, parent, false)
        return MonthlyExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MonthlyExpenseViewHolder, position: Int) {
        val currentItem = monthlyExpenses[position]
        holder.monthTextView.text =
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(currentItem.timestamp))
        // Set other TextViews
    }

    override fun getItemCount() = monthlyExpenses.size
}

class MonthlyExpenseDiffCallback(
    private val oldList: List<Budget>,
    private val newList: List<Budget>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timestamp == newList[newItemPosition].timestamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
