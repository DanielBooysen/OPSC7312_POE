package com.example.opsc7312_poe

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://dataservice.accuweather.com/"

// Define the API service interface
interface WeatherService {

    @GET("forecasts/v1/hourly/12hour/{locationKey}")
    fun getHourlyForecast(
        @Path("locationKey") locationKey: String, // Path parameter for location key
        @Query("apikey") apiKey: String,         // API key as query parameter
        @Query("metric") metric: Boolean = true, // Metric units for temperature and wind
        @Query("details") details: Boolean = true // Details boolean as query parameter
    ): Call<List<HourlyForecastResponse>>

    companion object {
        fun create(): WeatherService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(WeatherService::class.java)
        }
    }
}

// Data model for hourly forecast response
data class HourlyForecastResponse(
    val DateTime: String,
    val Temperature: Temperature,
    val WeatherIcon: Int,
    val IconPhrase: String,
    val Wind: Wind
)

data class Temperature(
    val Value: Double,
    val Unit: String
)

data class Wind(
    val Speed: Metric
)

data class Metric(
    val Value: Double,
    val Unit: String
)
