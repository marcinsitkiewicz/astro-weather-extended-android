package com.example.astroweatherextended

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.astroweatherextended.data.RetrofitClient
import com.example.astroweatherextended.data.models.OpenWeatherData
import com.example.astroweatherextended.fragments.*
import com.example.astroweatherextended.fragments.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var sunFragment: SunFragment ?= null
    private var moonFragment: MoonFragment ?= null
    private var refreshRate = 1
    private lateinit var latitude: String
    private lateinit var longitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        populateAdapter()
        initTabLayout()
        updatePreferences()

        handler.postDelayed(refreshTask, 1000)
    }

    private val refreshTask = object: Runnable {
        override fun run() {
            getWeatherData()
            sunFragment?.refreshFragment()
            moonFragment?.refreshFragment()
            handler.postDelayed(this, (refreshRate * 60000).toLong())
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
            if (resources.getBoolean(R.bool.isTablet))
                tab.text = adapter.mFragmentTitleList[position]
            else
                tab.setIcon(adapter.mFragmentIconList[position])
        }.attach()
    }

    fun getWeatherData() {
        val preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        RetrofitClient.apiService.getWeather(latitude, longitude, "minutely,hourly,alerts", "3796a47cdeac9263db6871ec76f6f8db", "metric").enqueue(object :
            Callback<OpenWeatherData> {
            override fun onResponse(call: Call<OpenWeatherData>, response: Response<OpenWeatherData>) {
                val weatherResponse = response.body()
                val gson = Gson()
                val weatherResponseString = gson.toJson(weatherResponse)

                preferences ?: return
                with (preferences.edit()) {
                    val cityNameKey = preferences.getString("city_key", "")
                    putString(cityNameKey, weatherResponseString)
                    apply()
                }
                Toast.makeText(this@MainActivity, "Weather updated", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<OpenWeatherData>, t: Throwable?) {
                Toast.makeText(this@MainActivity, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateAdapter() {
        viewPager = findViewById(R.id.viewPager)
        adapter = ViewPagerAdapter(this)

        adapter.addFragment(HomeFragment(), "HOME", R.drawable.ic_settings)
        adapter.addFragment(SunFragment(), "SUN", R.drawable.ic_sun)
        adapter.addFragment(MoonFragment(), "MOON", R.drawable.ic_moon)
        adapter.addFragment(CurrentFragment(), "CURRENT", R.drawable.ic_cloudy)
        adapter.addFragment(DetailsFragment(), "DETAILS", R.drawable.ic_wind)
        adapter.addFragment(ForecastFragment(), "FORECAST", R.drawable.ic_7day)

        viewPager.adapter = adapter

        sunFragment = adapter.getFragment("SUN") as SunFragment
        moonFragment = adapter.getFragment("MOON") as MoonFragment
    }

    fun updatePreferences() {
        val preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        refreshRate = preferences.getInt("refresh_value_key", 5)
        latitude = preferences.getString("latitude_key", "52").toString()
        longitude = preferences.getString("longitude_key", "19").toString()
    }
}