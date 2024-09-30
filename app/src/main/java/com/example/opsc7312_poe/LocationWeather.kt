package com.example.opsc7312_poe

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LocationWeather : AppCompatActivity() {

    // AccuWeather API key
    private val apiKey = "DecQ2sow31nZ8mKEqzB2BBdiRqurR8Ip"

    // Location keys for the dams
    private val locationKeys = mapOf(
        "Bronkhorspruit Dam" to "298085",
        "Hartbeespoort Dam" to "1147636",
        "Rietvlei Dam" to "305449",
        "Roodeplaat Dam" to "297972",
        "Vaal Dam" to "297930"
    )

    // Cache duration in milliseconds (1 hour)
    private val cacheDuration = 60 * 60 * 1000
    private var lastApiCallTime: Long = 0
    private var cachedForecasts: Map<String, List<HourlyForecastResponse>> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location_weather)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.locationWeatherView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val weatherService = WeatherService.create()

        // Fetch weather for all dams
        locationKeys.forEach { (damName, locationKey) ->
            fetchWeather(weatherService, damName, locationKey)
        }
    }

    private fun fetchWeather(service: WeatherService, damName: String, locationKey: String) {
        val currentTime = System.currentTimeMillis()

        // Check if we have cached data and if it's still valid
        if (cachedForecasts[damName] != null && currentTime - lastApiCallTime < cacheDuration) {
            // Use cached data
            displayWeather(damName, cachedForecasts[damName]?.firstOrNull())
        } else {
            // Make API call
            val call = service.getHourlyForecast(locationKey, apiKey)
            call.enqueue(object : Callback<List<HourlyForecastResponse>> {
                override fun onResponse(
                    call: Call<List<HourlyForecastResponse>>,
                    response: Response<List<HourlyForecastResponse>>
                ) {
                    if (response.isSuccessful) {
                        val weather = response.body()
                        // Cache the response
                        if (weather != null) {
                            cachedForecasts = cachedForecasts.toMutableMap().apply {
                                put(damName, weather)
                            }
                            lastApiCallTime = System.currentTimeMillis()
                        }
                        displayWeather(damName, weather?.firstOrNull())
                    } else {
                        Toast.makeText(this@LocationWeather, "Failed to retrieve data for $damName", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<HourlyForecastResponse>>, t: Throwable) {
                    Toast.makeText(this@LocationWeather, "Error fetching weather for $damName: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun displayWeather(damName: String, weather: HourlyForecastResponse?) {
        val weatherTextView = findViewById<TextView>(R.id.weatherTextView)
        if (weather != null) {
            val temp = weather.Temperature.Value
            val tempUnit = weather.Temperature.Unit
            val condition = weather.IconPhrase
            val windSpeed = weather.Wind.Speed.Value
            val windUnit = weather.Wind.Speed.Unit

            // Append the weather info for each dam to the TextView
            val weatherInfo = """
            $damName:
            Temperature: $temp$tempUnit
            Condition: $condition
            Wind: $windSpeed $windUnit
            ----------------------
        """.trimIndent()

            weatherTextView.append("$weatherInfo\n")
        } else {
            weatherTextView.append("$damName: No weather data available\n")
        }
    }
}