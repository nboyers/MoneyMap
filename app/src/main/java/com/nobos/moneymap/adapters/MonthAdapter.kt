package com.nobos.moneymap.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nobos.moneymap.R
import java.util.Calendar


class MonthAdapter(
    private val monthAbbreviations: Array<String>,
    private val onMonthClickListener: (Int, Int) -> Unit

) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    inner class MonthViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val monthView = TextView(parent.context)
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(8, 8, 8, 8)
        monthView.layoutParams = layoutParams
        monthView.gravity = Gravity.CENTER
        monthView.setTextColor(Color.WHITE)
        monthView.setBackgroundResource(R.drawable.square_month_background)
        monthView.setPadding(16, 16, 16, 16)
        return MonthViewHolder(monthView)
    }
    //TODO: Return a message for no data in the given month
    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val month = monthAbbreviations[position]
        holder.textView.text = month
        holder.textView.setOnClickListener {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val monthNumber = position + 1
            onMonthClickListener(monthNumber, currentYear)
        }
    }

    override fun getItemCount(): Int {
        return monthAbbreviations.size
    }
}
