package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val API_KEY = "af8b16741ab74e53544c95208262593b"  // OpenWeatherMap에서 발급받은 API 키를 입력하세요
    private val BASE_URL = "https://api.openweathermap.org/"
    private lateinit var cityNameTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityNameTextView = findViewById(R.id.cityNameTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)

        val location = "Seoul,KR"  // 원하는 위치 입력

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        val service = retrofit.create(OpenWeatherMapService::class.java)

        val call = service.getWeather(location, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    Log.d("weather", "Success: $weatherData")
                    val temperature = weatherData?.main?.temp
                    val description = weatherData?.weather?.get(0)?.description
                    val cityName = weatherData?.name

                    runOnUiThread {
                        cityNameTextView.text = weatherData?.name
                        temperatureTextView.text = "$temperature°C"
                        descriptionTextView.text = description
                    }
                } else {
                    Log.e("weather", "Error: ${response.code()} - ${response.message()}")
                    // API 호출은 성공했지만 응답이 실패한 경우 처리
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // 네트워크 오류 등의 실패한 경우 처리
                Log.e("weather", "Failure: ${t.message}")
            }
        })
    }
}