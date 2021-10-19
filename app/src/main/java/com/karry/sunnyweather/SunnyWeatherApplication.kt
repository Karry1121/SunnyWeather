package com.karry.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TOKEN = "" //彩云天气API令牌
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}