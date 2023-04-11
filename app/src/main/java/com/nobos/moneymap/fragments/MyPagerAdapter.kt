package com.nobos.moneymap.fragments


import ChartsFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SummaryFragment()
        1 -> ChartsFragment()
        else -> throw IllegalArgumentException("Invalid position")
    }
}