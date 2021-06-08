package com.example.astroweather.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import com.example.astroweather.R
import com.example.astroweather.R.layout.fragment_moon
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId

class MoonFragment : Fragment() {

    private var tvLongitude: TextView ?= null
    private var tvLatitude: TextView ?= null
    private var moonriseTime: TextView ?= null
    private var moonsetTime: TextView ?= null
    private var newMoonDate: TextView ?= null
    private var fullMoonDate: TextView ?= null
    private var moonPhase: TextView ?= null
    private var synodicDay: TextView ?= null
    private lateinit var astro : AstroCalculator
    private lateinit var astroDateTime : AstroDateTime
    private lateinit var astroLocation : AstroCalculator.Location
    private var latitude: Double = 52.0
    private var longitude: Double = 19.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragment_moon, container, false)
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
        tvLongitude = view?.findViewById(R.id.tv_moon_long)
        tvLatitude = view?.findViewById(R.id.tv_moon_lat)
        moonriseTime = view?.findViewById(R.id.tv_moonrise)
        moonsetTime = view?.findViewById(R.id.tv_moonset)
        newMoonDate = view?.findViewById(R.id.tv_new_moon)
        fullMoonDate = view?.findViewById(R.id.tv_full_moon)
        moonPhase = view?.findViewById(R.id.tv_moon_phase)
        synodicDay = view?.findViewById(R.id.tv_synodic_day)
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
        moonriseTime?.text = getMoonriseTime()?.let { reformatTime(it) }
        moonsetTime?.text = getMoonsetTime()?.let { reformatTime(it) }
        newMoonDate?.text = getNewMoonDate()?.let { reformatDate(it) }
        fullMoonDate?.text = getFullMoonDate()?.let { reformatDate(it) }
        moonPhase?.text = reformatPercent(getMoonPhase())
        synodicDay?.text = reformatDouble(getSynodicDay())
    }

    private fun reformatDate(astroDateTime: AstroDateTime): String {
        return String.format("%02d.%02d.%4d", astroDateTime.day, astroDateTime.month, astroDateTime.year)
    }

    private fun reformatTime(astroDateTime: AstroDateTime): String {
        return String.format("%02d:%02d:%02d", astroDateTime.hour, astroDateTime.minute, astroDateTime.second)
    }

    private fun reformatDouble(value: Double): String {
        val formatter: NumberFormat = DecimalFormat("#0.00000")
        return formatter.format(value)
    }

    private fun reformatPercent(azimuth: Double): String {
        val formatter: NumberFormat = DecimalFormat("#0.00")
        return formatter.format(azimuth * 100.0) + "%"
    }

    private fun getMoonriseTime(): AstroDateTime? {
        return astro.moonInfo.moonrise
    }

    private fun getMoonsetTime(): AstroDateTime? {
        return astro.moonInfo.moonset
    }

    private fun getNewMoonDate(): AstroDateTime? {
        return astro.moonInfo.nextNewMoon
    }

    private fun getFullMoonDate(): AstroDateTime? {
        return astro.moonInfo.nextFullMoon
    }

    private fun getMoonPhase(): Double {
        return astro.moonInfo.illumination
    }

    private fun getSynodicDay(): Double {
        return astro.moonInfo.age
    }

    private fun getLongitude(): Double {
        return astroLocation.longitude
    }

    private fun getLatitude(): Double {
        return astroLocation.latitude
    }
}