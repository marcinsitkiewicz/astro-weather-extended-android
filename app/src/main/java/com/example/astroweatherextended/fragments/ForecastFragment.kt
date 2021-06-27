package com.example.astroweatherextended.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.astroweatherextended.R
import com.example.astroweatherextended.data.models.OpenWeatherData
import com.example.astroweatherextended.fragments.adapters.ForecastAdapter
import com.google.gson.Gson

class ForecastFragment : Fragment() {
    private var tvLongitude: TextView?= null
    private var tvLatitude: TextView?= null
    private var city: TextView?= null
    private var tempUnit: String?= null
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherData: OpenWeatherData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTextViews()
        loadPreferences()

        val forecastAdapter = tempUnit?.let { ForecastAdapter(weatherData.daily, it) }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = forecastAdapter

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        initTextViews()
        loadPreferences()

        super.onResume()
    }

    private fun loadPreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        tvLatitude?.text = preferences?.getString("latitude_key", tvLatitude?.text.toString())
        tvLongitude?.text = preferences?.getString("longitude_key", tvLongitude?.text.toString())
        city?.text = preferences?.getString("city_key", city?.text.toString())
        tempUnit = preferences?.getString("temp_unit_key", "C")

        val weatherString = preferences?.getString(city?.text.toString(), "")
        weatherData = Gson().fromJson(weatherString, OpenWeatherData::class.java)
    }

    private fun initTextViews() {
        tvLongitude = view?.findViewById(R.id.tv_forecast_lat)
        tvLatitude = view?.findViewById(R.id.tv_forecast_long)
        city = view?.findViewById(R.id.tv_forecast_city)
        recyclerView = view?.findViewById(R.id.rv_forecast_recyclerView)!!
    }
}