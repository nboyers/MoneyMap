package com.nobos.moneymap.adapters
import androidx.recyclerview.widget.DiffUtil
import com.nobos.moneymap.models.budget

class DailyExpenseDiffCallback(
    private val oldList: List<budget>,
    private val newList: List<budget>
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
