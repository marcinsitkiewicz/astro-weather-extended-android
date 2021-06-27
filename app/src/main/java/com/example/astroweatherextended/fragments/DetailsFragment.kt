package com.example.astroweatherextended.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.astroweatherextended.R
import com.example.astroweatherextended.data.models.OpenWeatherData
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class DetailsFragment : Fragment() {
    private var tvLongitude: TextView?= null
    private var tvLatitude: TextView?= null
    private var city: TextView?= null
    private var weatherDirection: TextView?= null
    private var weatherSpeed: TextView?= null
    private var weatherGust: TextView?= null
    private var weatherHumidity: TextView?= null
    private var weatherVisibility: TextView?= null
    private var weatherCloudiness: TextView?= null
    private lateinit var weatherData: OpenWeatherData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTextViews()
        loadPreferences()

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

        val weatherString = preferences?.getString(city?.text.toString(), "")
        val gson = Gson()
        weatherData = gson.fromJson(weatherString, OpenWeatherData::class.java)
        weatherDirection?.text = weatherData.current.windDeg.toString()
        weatherSpeed?.text = weatherData.current.windSpeed.toString()
        weatherGust?.text = weatherData.current.windGust.toString()
        weatherHumidity?.text = weatherData.current.humidity.toString()
        weatherCloudiness?.text = weatherData.current.clouds.toString()
    }

    private fun initTextViews() {
        tvLatitude = view?.findViewById(R.id.tv_details_lat)
        tvLongitude = view?.findViewById(R.id.tv_details_long)
        city = view?.findViewById(R.id.tv_details_city)
        weatherDirection = view?.findViewById(R.id.tv_details_wind_dir)
        weatherSpeed = view?.findViewById(R.id.tv_details_wind_speed)
        weatherGust = view?.findViewById(R.id.tv_details_wind_gust)
        weatherHumidity = view?.findViewById(R.id.tv_details_hum)
        weatherVisibility = view?.findViewById(R.id.tv_details_vis)
        weatherCloudiness = view?.findViewById(R.id.tv_details_cloud)
    }
}