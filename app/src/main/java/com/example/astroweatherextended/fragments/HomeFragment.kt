package com.example.astroweather.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.astroweather.MainActivity
import com.example.astroweather.R

class HomeFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var latitude: EditText
    private lateinit var longitude: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spinner = view.findViewById(R.id.sp_refresh_rate)
        saveButton = view.findViewById(R.id.btn_save)
        longitude = view.findViewById(R.id.et_long)
        latitude = view.findViewById(R.id.et_lat)

        loadPreferences()

        saveButton.setOnClickListener {
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

            savePreferences()
            (activity as MainActivity).updatePreferences()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun areCoordsValid(lat: Double, long: Double): Boolean {
        return ((lat >= -90 && lat <= 90) && (long >= -180 && long <= 180))
    }

    private fun savePreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE) ?: return
        with (preferences.edit()) {
            val spinnerValue = Integer.parseInt(spinner.selectedItem.toString())
            val spinnerPosition = spinner.selectedItemPosition

            putInt("refresh_value_key", spinnerValue)
            putInt("refresh_position_key", spinnerPosition)
            putString("latitude_key", latitude.text.toString())
            putString("longitude_key", longitude.text.toString())
            apply()
        }
    }

    private fun loadPreferences() {
        val preferences = activity?.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val spinnerPosition = preferences?.getInt("refresh_position_key", 0)
        val latitudeKey = preferences?.getString("latitude_key", latitude.text.toString())
        val longitudeKey = preferences?.getString("longitude_key", longitude.text.toString())

        if (spinnerPosition != null) {
            spinner.setSelection(spinnerPosition)
        }
        latitude.setText(latitudeKey)
        longitude.setText(longitudeKey)

    }

}