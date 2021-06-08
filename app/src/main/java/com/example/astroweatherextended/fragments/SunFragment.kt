package com.example.astroweatherextended.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import com.example.astroweatherextended.R
import com.example.astroweatherextended.R.layout.fragment_sun
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId

class SunFragment : Fragment() {

    private var tvLongitude: TextView ?= null
    private var tvLatitude: TextView ?= null
    private var sunriseTime: TextView ?= null
    private var sunriseAzimuth: TextView ?= null
    private var sunsetTime: TextView ?= null
    private var sunsetAzimuth: TextView ?= null
    private var duskTime: TextView ?= null
    private var twilightTime: TextView ?= null
    private lateinit var astro: AstroCalculator
    private lateinit var astroDateTime: AstroDateTime
    private lateinit var astroLocation: AstroCalculator.Location
    private var latitude: Double = 52.0
    private var longitude: Double = 19.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragment_sun, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTextViews()
        loadPreferences()
        refreshFragment()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        initTextViews()
        loadPreferences()
        refreshFragment()

        super.onResume()
    }

    private fun initTextViews() {
        tvLongitude = view?.findViewById(R.id.tv_sun_long)
        tvLatitude = view?.findViewById(R.id.tv_sun_lat)
        sunriseTime = view?.findViewById(R.id.tv_sunrise_time)
        sunriseAzimuth = view?.findViewById(R.id.tv_sunrise_azimuth)
        sunsetTime = view?.findViewById(R.id.tv_sunset_time)
        sunsetAzimuth = view?.findViewById(R.id.tv_sunset_azimuth)
        duskTime = view?.findViewById(R.id.tv_dusk_time)
        twilightTime = view?.findViewById(R.id.tv_twilight_time)
    }

    fun loadPreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val latitudeKey = preferences?.getString("latitude_key", latitude.toString())
        val longitudeKey = preferences?.getString("longitude_key", longitude.toString())
        latitude = latitudeKey?.toDouble()!!
        longitude = longitudeKey?.toDouble()!!
    }

    fun refreshFragment() {
        updateAstro()
        updateTexts()
    }

    private fun updateAstro() {
        updateDateTime()
        updateLocation(latitude, longitude)
        astro = AstroCalculator(astroDateTime, astroLocation)
    }

    private fun updateDateTime() {
        val zoneId = ZoneId.of("Europe/Warsaw")
        val localDateTime = LocalDateTime.now(zoneId)
        astroDateTime = AstroDateTime(
            localDateTime.year,
            localDateTime.monthValue,
            localDateTime.dayOfMonth,
            localDateTime.hour,
            localDateTime.minute,
            localDateTime.second,
            2,
            false
        )
    }

    private fun updateLocation(lat: Double, long: Double) {
        astroLocation = AstroCalculator.Location(lat, long)
    }

    private fun updateTexts() {
        tvLongitude?.text = reformatDouble(getLongitude())
        tvLatitude?.text = reformatDouble(getLatitude())
        sunriseTime?.text = getSunriseTime()?.let { reformatTime(it) }
        sunriseAzimuth?.text = reformatDouble(getSunriseAzimuth()) + "°"
        sunsetTime?.text = getSunsetTime()?.let { reformatTime(it) }
        sunsetAzimuth?.text = reformatDouble(getSunsetAzimuth()) + "°"
        duskTime?.text = getDusk()?.let { reformatTime(it) }
        twilightTime?.text = getTwilight()?.let { reformatTime(it) }
    }

    private fun reformatTime(astroDateTime: AstroDateTime): String {
        return String.format("%02d:%02d:%02d", astroDateTime.hour, astroDateTime.minute, astroDateTime.second)
    }

    private fun reformatDouble(value: Double): String {
        val formatter: NumberFormat = DecimalFormat("#0.00000")
        return formatter.format(value)
    }

    private fun getSunriseTime(): AstroDateTime? {
        return astro.sunInfo.sunrise
    }

    private fun getSunsetTime(): AstroDateTime? {
        return astro.sunInfo.sunset
    }

    private fun getSunriseAzimuth(): Double {
        return astro.sunInfo.azimuthRise
    }

    private fun getSunsetAzimuth(): Double {
        return astro.sunInfo.azimuthSet
    }

    private fun getDusk(): AstroDateTime? {
        return astro.sunInfo.twilightMorning
    }

    private fun getTwilight(): AstroDateTime? {
        return astro.sunInfo.twilightEvening
    }

    private fun getLongitude(): Double {
        return astroLocation.longitude
    }

    private fun getLatitude(): Double {
        return astroLocation.latitude
    }
}