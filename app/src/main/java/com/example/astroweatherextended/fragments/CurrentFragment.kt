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

class CurrentFragment : Fragment() {
    private var tempUnit: String = ""
    private var tvLongitude: TextView?= null
    private var tvLatitude: TextView?= null
    private var city: TextView?= null
    private var weatherImage: ImageView?= null
    private var weatherDesc: TextView?= null
    private var weatherTemp: TextView?= null
    private var weatherFeelsTemp: TextView?= null
    private var weatherPressure: TextView?= null
    private lateinit var weatherData: OpenWeatherData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_current, container, false)
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

    private fun convertTemp(value: Double): Int {
        return if (tempUnit == "F")
            ((value * 9.0 / 5.0) + 32.0).toInt()
        else
            value.toInt()
    }

    private fun loadPreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        tvLatitude?.text = preferences?.getString("latitude_key", tvLatitude?.text.toString())
        tvLongitude?.text = preferences?.getString("longitude_key", tvLongitude?.text.toString())
        city?.text = preferences?.getString("city_key", city?.text.toString())
        tempUnit = preferences?.getString("temp_unit_key", "C").toString()

        val weatherString = preferences?.getString(city?.text.toString(), "")
        val gson = Gson()
        weatherData = gson.fromJson(weatherString, OpenWeatherData::class.java)
        weatherDesc?.text = weatherData.current.weather[0].description

        val tempValue = convertTemp(weatherData.current.temp).toString() + "°" + tempUnit
        val feelsValue = convertTemp(weatherData.current.feelsLike).toString() + "°" + tempUnit

        weatherTemp?.text = tempValue
        weatherFeelsTemp?.text = feelsValue
        weatherPressure?.text = weatherData.current.pressure.toString()

        when (weatherData.current.weather[0].icon) {
            "01d" -> weatherImage?.setImageResource(R.drawable.icon_01d)
            "01n" -> weatherImage?.setImageResource(R.drawable.icon_01n)
            "02d" -> weatherImage?.setImageResource(R.drawable.icon_02d)
            "02n" -> weatherImage?.setImageResource(R.drawable.icon_02n)
            "03d" -> weatherImage?.setImageResource(R.drawable.icon_03d)
            "03n" -> weatherImage?.setImageResource(R.drawable.icon_03n)
            "04d" -> weatherImage?.setImageResource(R.drawable.icon_04d)
            "04n" -> weatherImage?.setImageResource(R.drawable.icon_04n)
            "09d" -> weatherImage?.setImageResource(R.drawable.icon_09d)
            "09n" -> weatherImage?.setImageResource(R.drawable.icon_09n)
            "10d" -> weatherImage?.setImageResource(R.drawable.icon_10d)
            "10n" -> weatherImage?.setImageResource(R.drawable.icon_10n)
            "11d" -> weatherImage?.setImageResource(R.drawable.icon_11d)
            "11n" -> weatherImage?.setImageResource(R.drawable.icon_11n)
            "13d" -> weatherImage?.setImageResource(R.drawable.icon_13d)
            "13n" -> weatherImage?.setImageResource(R.drawable.icon_13n)
            "50d" -> weatherImage?.setImageResource(R.drawable.icon_50d)
            "50n" -> weatherImage?.setImageResource(R.drawable.icon_50n)
        }
    }

    private fun initTextViews() {
        tvLatitude = view?.findViewById(R.id.tv_current_lat)
        tvLongitude = view?.findViewById(R.id.tv_current_long)
        city = view?.findViewById(R.id.tv_current_city)
        weatherImage = view?.findViewById(R.id.iv_icon)
        weatherDesc = view?.findViewById(R.id.tv_current_desc)
        weatherTemp = view?.findViewById(R.id.tv_current_temp)
        weatherFeelsTemp = view?.findViewById(R.id.tv_current_feel_temp)
        weatherPressure = view?.findViewById(R.id.tv_current_press)
    }
}