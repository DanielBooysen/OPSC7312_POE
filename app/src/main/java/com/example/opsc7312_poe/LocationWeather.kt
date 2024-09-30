package com.example.opsc7312_poe

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
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
    private val apiKey = "xzLgGLlJGYuYhFgLLa1HmvUIC2lyAM6G"

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

        // Sort the location keys alphabetically by dam names
        val sortedLocationKeys = locationKeys.toSortedMap(String.CASE_INSENSITIVE_ORDER)

        // Fetch weather for all dams in alphabetical order
        sortedLocationKeys.forEach { (damName, locationKey) ->
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
        val layout = findViewById<LinearLayout>(R.id.weatherLayout) // Get the root layout

        // Create a new layout for each dam's weather information
        val damLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        if (weather != null) {
            val temp = weather.Temperature.Value
            val tempUnit = weather.Temperature.Unit
            val condition = weather.IconPhrase
            val windSpeed = weather.Wind.Speed.Value
            val windUnit = weather.Wind.Speed.Unit
            val iconNumber = weather.WeatherIcon

            // Create TextView for dam's weather info
            val weatherInfo = """
            $damName:
            Temperature: $temp$tempUnit
            Condition: $condition
            Wind: $windSpeed $windUnit
        """.trimIndent()
            val weatherTextView = TextView(this).apply {
                text = weatherInfo
                textSize = 16f
            }

            // Create ImageView for weather icon
            val weatherIconView = ImageView(this)
            val iconResourceName = String.format(Locale.getDefault(), "aw%02d", iconNumber)
            val resId = resources.getIdentifier(iconResourceName, "drawable", packageName)
            if (resId != 0) {
                weatherIconView.setImageResource(resId)
            } else {
                // Fallback to aw01.png if no valid icon is found
                val defaultResId = resources.getIdentifier("aw01", "drawable", packageName)
                weatherIconView.setImageResource(defaultResId)
            }

            // Add views to the dam layout
            damLayout.addView(weatherIconView) // Add the icon
            damLayout.addView(weatherTextView) // Add the weather info
        } else {
            val weatherTextView = TextView(this).apply {
                text = "$damName: No weather data available"
                textSize = 16f
            }
            damLayout.addView(weatherTextView)
        }

        // Add each dam layout to the main layout
        layout.addView(damLayout)
    }
}