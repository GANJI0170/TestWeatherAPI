package com.example.myapplication

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val name: String
)

data class Main(
    val temp: Double,
    val humidity: Int
)
data class Clouds(
    val all: Int
)
data class Wind(
    val speed: Double
)
data class Weather(
    val main: String,
    val description: String,
    val icon: String
)