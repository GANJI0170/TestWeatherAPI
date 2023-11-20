package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private val API_KEY = "af8b16741ab74e53544c95208262593b"  // OpenWeatherMap에서 발급받은 API 키를 입력하세요
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val iconBaseUrl = "https://openweathermap.org/img/w/"
    private lateinit var cityNameTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var weatherIconView: ImageView
    /*private lateinit var cloudinessView: TextView
    private lateinit var humidityView: TextView
    private lateinit var windSpeedView: TextView*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityNameTextView = findViewById(R.id.cityNameTextView)
        temperatureTextView = findViewById(R.id.temperatureTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        weatherIconView = findViewById(R.id.weatherIconView)
       /*
        cloudinessView = findViewById(R.id.cloudinessView)
        humidityView = findViewById(R.id.humidityView)
        windSpeedView = findViewById(R.id.windSpeedView)
*/
        val location = "Seoul,KR"  // 원하는 위치 입력

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()

        val service = retrofit.create(OpenWeatherMapService::class.java)

        val call = service.getWeather(location, API_KEY)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    Log.d("weather", "Success: $weatherData")
                    val temperatureKelvin = weatherData?.main?.temp
                    val temperatureCelsius = temperatureKelvin?.minus(273.15)?.roundToInt()
                    val description = weatherData?.weather?.get(0)?.description
                    val cityName = weatherData?.name
                    val iconCode = weatherData?.weather?.getOrNull(0)?.icon
                    val iconUrl = "$iconBaseUrl$iconCode.png"

                    val koreanDescription = convertToKorean(description)
                    val cloudiness = weatherData?.clouds?.all
                    val humidity = weatherData?.main?.humidity
                    val windSpeed = weatherData?.wind?.speed
                    runOnUiThread {
                        cityNameTextView.text = "도시 이름 : $cityName "
                        temperatureTextView.text = "현재 기온 : $temperatureCelsius°C"
                        descriptionTextView.text = "현재 날씨 : $koreanDescription\n"  +
                                "구름양: $cloudiness%\n" +
                                "습도: $humidity%\n" +
                                "바람 속도: $windSpeed m/s"


                        Glide.with(this@MainActivity)
                            .load(iconUrl)
                            .into(weatherIconView)
                        Log.d("weather", "Icon URL: $iconUrl")
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
    // 날씨 상태를 한글로 변환하는 함수
    private fun convertToKorean(description: String?): String {
        return when (description?.toLowerCase(Locale.getDefault())) {
            "clear sky" -> "맑음"
            "few clouds" -> "구름 조금"
            "scattered clouds" -> "구름이 조금 흩어짐"
            "broken clouds", "overcast clouds" -> "구름이 많음"
            "shower rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain" -> "비"
            "thunderstorm", "thunderstorm with light rain", "thunderstorm with rain", "thunderstorm with heavy rain" -> "천둥번개"
            "snow", "light snow", "sleet", "light shower sleet", "shower sleet", "light rain and snow", "rain and snow", "light shower snow", "shower snow", "heavy shower snow" -> "눈"
            "mist", "smoke", "haze", "dust", "fog", "sand", "dust", "volcanic ash", "squalls", "tornado" -> "안개"
            else -> description ?: ""
        }
    }
}