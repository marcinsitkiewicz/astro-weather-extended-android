package com.example.astroweatherextended.fragments.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.FragmentActivity

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    var mFragmentTitleList = ArrayList<String>()
    private var mFragmentList = ArrayList<Fragment>()


    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun getFragment(title: String): Fragment {
        return mFragmentList[mFragmentTitleList.indexOf(title)]
    }
}


