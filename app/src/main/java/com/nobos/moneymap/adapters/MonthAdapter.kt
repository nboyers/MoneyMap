package com.nobos.moneymap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nobos.moneymap.R
import java.util.Calendar


class MonthAdapter(
    private val monthAbbreviations: Array<String>,
    private val onMonthClickListener: (Int, Int) -> Unit
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.monthTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_monthly_expense, parent, false)
        return MonthViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = monthAbbreviations[position]
        holder.textView.text = month
        holder.itemView.setOnClickListener {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val monthNumber = position + 1

            onMonthClickListener(monthNumber, currentYear)
        }
    }

    override fun getItemCount(): Int {
        return monthAbbreviations.size
    }
}
