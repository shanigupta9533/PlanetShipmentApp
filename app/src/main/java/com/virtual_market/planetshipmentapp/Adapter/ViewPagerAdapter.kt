package com.virtual_market.planetshipmentapp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class ViewPagerAdapter(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val stringList: MutableList<String> = ArrayList()
    fun addFragment(fragment: Fragment, s: String) {
        fragmentList.add(fragment)
        stringList.add(s)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return stringList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}