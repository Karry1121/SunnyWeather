package com.karry.sunnyweather.ui.weather

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.karry.sunnyweather.R
import com.karry.sunnyweather.logic.model.Weather
import com.karry.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {


    private val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showWeatherInfo(weather: Weather) {
        val toolbarNow: MaterialToolbar = findViewById(R.id.toolbar_now)
        val currentTemp: MaterialTextView = findViewById(R.id.currentTemp)
        val currentSky: MaterialTextView = findViewById(R.id.currentSky)
        val currentAQI: MaterialTextView = findViewById(R.id.currentAQI)
        val coldRiskText: MaterialTextView = findViewById(R.id.coldRiskText)
        val dressingText: MaterialTextView = findViewById(R.id.dressingText)
        val ultravioletText: MaterialTextView = findViewById(R.id.ultravioletText)
        val carWashingText: MaterialTextView = findViewById(R.id.carWashingText)

        val nowLayout: ConstraintLayout = findViewById(R.id.nowLayout)
        val forecastLayout: ConstraintLayout = findViewById(R.id.forecastLayout)
        val weatherLayout: ScrollView = findViewById(R.id.weatherLayout)
        toolbarNow.title = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml的布局
        val currentTempText = "${realtime.temperature.toInt()}℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        currentAQI.text = "空气质量指数 ${realtime.airQuality.aqi.chn.toInt()}"
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as MaterialTextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ShapeableImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as MaterialTextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as MaterialTextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}