package com.nobos.moneymap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nobos.moneymap.R
import com.nobos.moneymap.models.budget
import java.text.SimpleDateFormat
import java.util.*

class DailyExpenseAdapter(
    private var dailyExpenses: List<budget>,
    private val onItemClicked: (budget) -> Unit
) : RecyclerView.Adapter<DailyExpenseAdapter.DailyExpenseViewHolder>() {

    inner class DailyExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        // Add other TextViews

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(dailyExpenses[position])
                }
            }
        }
    }
    fun updateDailyExpenses(newExpenses: List<budget>) {
        val diffCallback = DailyExpenseDiffCallback(dailyExpenses, newExpenses)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dailyExpenses = newExpenses
        diffResult.dispatchUpdatesTo(this)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_expense, parent, false)
        return DailyExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DailyExpenseViewHolder, position: Int) {
        val currentItem = dailyExpenses[position]
        holder.dateTextView.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(currentItem.timestamp))
        // Set other TextViews
    }

    override fun getItemCount() = dailyExpenses.size
}
