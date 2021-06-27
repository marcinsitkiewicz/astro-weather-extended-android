package com.example.astroweatherextended.data

import com.example.astroweatherextended.data.models.OpenWeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("data/2.5/onecall")
    fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("exclude") exclude: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<OpenWeatherData>
}