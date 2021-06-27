package com.example.astroweatherextended.fragments

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.astroweatherextended.MainActivity
import com.example.astroweatherextended.R
import com.example.astroweatherextended.data.RetrofitClient
import com.example.astroweatherextended.data.models.OpenWeatherData
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.activityUiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var spinnerCities: Spinner
    private lateinit var latitude: EditText
    private lateinit var longitude: EditText
    private lateinit var cityName: EditText
    private lateinit var connInfo: TextView
    private lateinit var saveButton: Button
    private lateinit var setCityButton: Button
    private lateinit var saveCityButton: Button
    private lateinit var removeCityButton: Button
    private lateinit var updateButton: Button
    private lateinit var toggleButton: ToggleButton
    private var citiesList: MutableSet<String> = mutableSetOf()
    private lateinit var weatherData: OpenWeatherData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinner = view.findViewById(R.id.sp_refresh_rate)
        spinnerCities = view.findViewById(R.id.sp_favorites)
        saveButton = view.findViewById(R.id.btn_save)
        longitude = view.findViewById(R.id.et_long)
        latitude = view.findViewById(R.id.et_lat)
        cityName = view.findViewById(R.id.et_city_name)
        setCityButton = view.findViewById(R.id.btn_set_city)
        saveCityButton = view.findViewById(R.id.btn_save_city)
        removeCityButton = view.findViewById(R.id.btn_remove_city)
        updateButton = view.findViewById(R.id.btn_update)
        toggleButton = view.findViewById(R.id.tb_temp_unit)
        connInfo = view.findViewById(R.id.tv_conn_info)

        loadPreferences()
        if (isInternetConnected() == false) {
            connInfo.text = "No internet connection. You can only view data from your favorite cities."
        }
        else {
            connInfo.text = ""
        }

        saveButton.setOnClickListener {
            if (isInternetConnected() == false) {
                Toast.makeText(activity, "No internet connection", LENGTH_SHORT).show()
                connInfo.text = "No internet connection. You can only view data from your favorite cities."
                return@setOnClickListener
            }

            connInfo.text = ""
            val lat = latitude.text.toString()
            val long = longitude.text.toString()

            if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(long)) {
                Toast.makeText(activity, "Coords field empty", LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (!areCoordsValid(lat.toDouble(), long.toDouble())) {
                Toast.makeText(activity, "Coords field invalid value\nLatitude = <-90, 90>\nLongitude = <-180, 180>", LENGTH_LONG).show()
                return@setOnClickListener
            }

            doAsync {
                setAddressFromLocation(lat.toDouble(), long.toDouble())
            }
            savePreferences()
        }

        setCityButton.setOnClickListener {
            if (isInternetConnected() == false) {
                Toast.makeText(activity, "No internet connection", LENGTH_SHORT).show()
                connInfo.text = "No internet connection. You can only view data from your favorite cities."
                return@setOnClickListener
            }

            connInfo.text = ""
            val city = cityName.text.toString()

            if (TextUtils.isEmpty(city)) {
                Toast.makeText(activity, "City name field empty", LENGTH_SHORT).show()
                return@setOnClickListener
            }

            doAsync {
                setLocationFromAddress(city)
            }
            savePreferences()
        }

        saveCityButton.setOnClickListener {
            if (cityName.text.toString().isEmpty() || latitude.text.toString().isEmpty() || longitude.text.toString().isEmpty()) {
                Toast.makeText(activity, "Fill in empty fields above", LENGTH_SHORT).show()
                return@setOnClickListener
            }
            citiesList.add(cityName.text.toString())
            val citiesArray = citiesList.toList()
            val setAdapter = ArrayAdapter(activity as MainActivity, android.R.layout.simple_spinner_item, citiesArray)
            spinnerCities.adapter = setAdapter
            spinnerCities.setSelection(citiesArray.indexOf(cityName.text.toString()))
//            (activity as MainActivity).getWeatherData()
            savePreferences()
        }

        removeCityButton.setOnClickListener {
            citiesList.remove(spinnerCities.selectedItem)
            val citiesArray = citiesList.toList()
            val setAdapter = ArrayAdapter(activity as MainActivity, android.R.layout.simple_spinner_item, citiesArray)
            spinnerCities.adapter = setAdapter
            savePreferences()
        }

        updateButton.setOnClickListener {
            savePreferences()
            (activity as MainActivity).updatePreferences()
            (activity as MainActivity).getWeatherData()
        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
                with (preferences.edit()) {
                    putString("temp_unit_key", "F")
                    putBoolean("toogle_is_checked_key", isChecked)
                    apply()
                }
            }
            else {
                val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
                with (preferences.edit()) {
                    putString("temp_unit_key", "C")
                    putBoolean("toogle_is_checked_key", isChecked)
                    apply()
                }
            }
        }

        var currentSelection = spinnerCities.selectedItemPosition
        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (currentSelection != position) {
                    val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                    val weatherString = preferences?.getString(spinnerCities.selectedItem.toString(), "")
                    if (weatherString.equals("") || weatherString.equals("null")) {
                        Toast.makeText(activity, "City is not available anymore", LENGTH_SHORT).show()
                        citiesList.remove(spinnerCities.selectedItem)
                        val citiesArray = citiesList.toList()
                        val setAdapter = ArrayAdapter(activity as MainActivity, android.R.layout.simple_spinner_item, citiesArray)
                        spinnerCities.adapter = setAdapter
                        savePreferences()
                        return
                    }
                    cityName.setText(spinnerCities.selectedItem.toString())
                    val gson = Gson()
                    weatherData = gson.fromJson(weatherString, OpenWeatherData::class.java)
                    latitude.setText(weatherData.lat.toString())
                    longitude.setText(weatherData.lon.toString())
                    savePreferences()
                }
                currentSelection = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        if (isInternetConnected() == false) {
            connInfo.text = "No internet connection. You can only view data from your favorite cities."
        }
        else {
            connInfo.text = ""
        }
        super.onResume()
    }

    private fun setAddressFromLocation(lat: Double, lng: Double) {
        val geocoder = Geocoder(activity, Locale.ENGLISH)
        val list = geocoder.getFromLocation(lat, lng, 1)
        if (list != null && list.isNotEmpty())
            activity?.runOnUiThread {
                cityName.setText(list[0].locality.toString())
            }
        else
            activity?.runOnUiThread {
                Toast.makeText(activity, "Couldn't find city name from given coordinates", LENGTH_SHORT).show()
            }
    }

    private fun setLocationFromAddress(name: String) {
        val geocoder = Geocoder(activity, Locale.ENGLISH)
        val list = geocoder.getFromLocationName(name, 1)
        if (list != null && list.isNotEmpty()) {
            activity?.runOnUiThread {
                val lat = BigDecimal(list[0].latitude).setScale(4, BigDecimal.ROUND_HALF_UP)
                latitude.setText(lat.toString())

                val lon = BigDecimal(list[0].longitude).setScale(4, BigDecimal.ROUND_HALF_UP)
                longitude.setText(lon.toString())
            }
        }
        else {
            activity?.runOnUiThread {
                Toast.makeText(activity, "Couldn't find city coordinates from given name", LENGTH_SHORT).show()
            }
        }
    }

    private fun isInternetConnected(): Boolean {
        val connManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    private fun areCoordsValid(lat: Double, long: Double): Boolean {
        return ((lat >= -90 && lat <= 90) && (long >= -180 && long <= 180))
    }

    private fun savePreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE) ?: return
        with (preferences.edit()) {
            val spinnerValue = Integer.parseInt(spinner.selectedItem.toString())
            val spinnerPosition = spinner.selectedItemPosition

            if (spinnerCities.count > 0) {
                val citySpinnerValue = spinnerCities.selectedItem.toString()
                val citySpinnerPosition = spinnerCities.selectedItemPosition
                putString("city_value_key", citySpinnerValue)
                putInt("city_position_key", citySpinnerPosition)
            }

            putStringSet("favoritesSet", citiesList)
            putInt("refresh_value_key", spinnerValue)
            putInt("refresh_position_key", spinnerPosition)
            putString("latitude_key", latitude.text.toString())
            putString("longitude_key", longitude.text.toString())
            putString("city_key", cityName.text.toString())
            apply()
        }
    }

    private fun loadPreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val spinnerPosition = preferences?.getInt("refresh_position_key", 0)
        val citySpinnerPosition = preferences?.getInt("city_position_key", 0)
        val latitudeKey = preferences?.getString("latitude_key", latitude.text.toString())
        val longitudeKey = preferences?.getString("longitude_key", longitude.text.toString())
        val cityNameKey = preferences?.getString("city_key", cityName.text.toString())
        citiesList = preferences?.getStringSet("favoritesSet", citiesList) as MutableSet<String>
        val isToggleChecked = preferences.getBoolean("toogle_is_checked_key", false)

        if (spinnerPosition != null) {
            spinner.setSelection(spinnerPosition)
        }
        if (citySpinnerPosition != null) {
            val citiesArray = citiesList.toList()
            val setAdapter = ArrayAdapter(activity as MainActivity, android.R.layout.simple_spinner_item, citiesArray)
            spinnerCities.adapter = setAdapter
            spinnerCities.setSelection(citySpinnerPosition)
        }
        toggleButton.isChecked = isToggleChecked

        latitude.setText(latitudeKey)
        longitude.setText(longitudeKey)
        cityName.setText(cityNameKey)
    }

}