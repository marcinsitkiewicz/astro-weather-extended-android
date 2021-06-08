package com.example.astroweatherextended

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.astroweatherextended.fragments.HomeFragment
import com.example.astroweatherextended.fragments.MoonFragment
import com.example.astroweatherextended.fragments.SunFragment
import com.example.astroweatherextended.fragments.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private var isDeviceTablet: Boolean ?= null
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var sunFragment: SunFragment ?= null
    private var moonFragment: MoonFragment ?= null
    private var refreshRate = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isDeviceTablet = resources.getBoolean(R.bool.isTablet)
        if (isDeviceTablet == true) {
            sunFragment = supportFragmentManager.findFragmentById(R.id.fr_sun) as SunFragment
            moonFragment = supportFragmentManager.findFragmentById(R.id.fr_moon) as MoonFragment
        }
        else {
            populateAdapter()
            initTabLayout()
        }

        handler.postDelayed(refreshTask, 1000)
    }

    private val refreshTask = object: Runnable {
        override fun run() {
//            Toast.makeText(this@MainActivity, "Update", LENGTH_SHORT).show()
            sunFragment?.refreshFragment()
            moonFragment?.refreshFragment()
            handler.postDelayed(this, (refreshRate * 1000).toLong())
        }
    }

    override fun onPause() {
        handler.removeCallbacks(refreshTask)
        super.onPause()
    }

    override fun onResume() {
        handler.postDelayed(refreshTask, 1000)
        super.onResume()
    }

    private fun initTabLayout() {
        tabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab, position ->
            tab.text = adapter.mFragmentTitleList[position]
        }.attach()
    }

    private fun populateAdapter() {
        viewPager = findViewById(R.id.viewPager)
        adapter = ViewPagerAdapter(this)

        adapter.addFragment(HomeFragment(), "HOME")
        adapter.addFragment(SunFragment(), "SUN")
        adapter.addFragment(MoonFragment(), "MOON")

        viewPager.adapter = adapter

        sunFragment = adapter.getFragment("SUN") as SunFragment
        moonFragment = adapter.getFragment("MOON") as MoonFragment
    }

    fun updatePreferences() {
        val preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        refreshRate = preferences.getInt("refresh_value_key", 5)

        if (isDeviceTablet == true) {
            sunFragment?.loadPreferences()
            sunFragment?.refreshFragment()

            moonFragment?.loadPreferences()
            moonFragment?.refreshFragment()
        }
    }
}