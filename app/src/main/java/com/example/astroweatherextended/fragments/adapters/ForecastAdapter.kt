package com.example.astroweatherextended.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.astroweatherextended.R
import com.example.astroweatherextended.data.models.Daily
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ForecastAdapter(private val dataSet: List<Daily>, unit: String) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    private val tempUnit: String = unit

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val forecastDescription: TextView = view.findViewById(R.id.tv_item_description)
        val forecastDate: TextView = view.findViewById(R.id.tv_item_date)
        val forecastTempMax: TextView = view.findViewById(R.id.tv_item_temp_max)
        val forecastTempMin: TextView = view.findViewById(R.id.tv_item_temp_min)
        val forecastIcon: ImageView = view.findViewById(R.id.iv_item_icon)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycler_view_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when (dataSet[position].weather[0].icon) {
            "01d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_01d)
            "01n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_01n)
            "02d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_02d)
            "02n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_02n)
            "03d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_03d)
            "03n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_03n)
            "04d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_04d)
            "04n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_04n)
            "09d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_09d)
            "09n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_09n)
            "10d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_10d)
            "10n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_10n)
            "11d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_11d)
            "11n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_11n)
            "13d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_13d)
            "13n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_13n)
            "50d" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_50d)
            "50n" -> viewHolder.forecastIcon.setImageResource(R.drawable.icon_50n)
        }

        viewHolder.forecastDescription.text = dataSet[position].weather[0].main
        val dateFormat = SimpleDateFormat("EEEE, d/MM")
        val dateString = dataSet[position].dt
        viewHolder.forecastDate.text = dateFormat.format(Date(dateString * 1000)).toString()

        val maxValue = convertTemp(dataSet[position].temp.max).toString()
        val minValue = convertTemp(dataSet[position].temp.min).toString()

        val tempMax = "$maxValue°$tempUnit / "
        viewHolder.forecastTempMax.text = tempMax
        val tempMin = "$minValue°$tempUnit"
        viewHolder.forecastTempMin.text = tempMin
    }

    override fun getItemCount() = dataSet.size

    private fun convertTemp(value: Double): Int {
        return if (tempUnit == "F")
            ((value * 9.0 / 5.0) + 32.0).toInt()
        else
            value.toInt()
    }
}