package com.example.coursework.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    suspend fun getWeather(
        @Query("key") apiKey:String,
        @Query("q") location:String,
    ):WeatherData
}

object WeatherApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weatherService:WeatherApiService = retrofit.create(WeatherApiService::class.java)
}