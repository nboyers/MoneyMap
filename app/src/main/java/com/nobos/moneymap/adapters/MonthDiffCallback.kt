package com.nobos.moneymap.adapters

import androidx.recyclerview.widget.DiffUtil
import com.nobos.moneymap.models.Budget
import java.time.Month


class MonthDiffCallback(
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
