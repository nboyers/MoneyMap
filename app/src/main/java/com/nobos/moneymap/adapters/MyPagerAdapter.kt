package com.nobos.moneymap.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nobos.moneymap.fragments.ChartsFragment
import com.nobos.moneymap.fragments.SummaryFragment

class MyPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private var fragments = listOf(
        SummaryFragment.newInstance(),
        ChartsFragment.newInstance()
    )

    fun replaceChartsFragment(newFragment: Fragment) {
        fragments = fragments.toMutableList().apply {
            set(1, newFragment)
        }
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
