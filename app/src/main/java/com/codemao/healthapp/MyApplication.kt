package com.codemao.healthapp

import android.app.Activity
import android.app.Application
import com.codemao.sensors.SensorsHelper

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        SensorsHelper.init(this,BuildConfig.DEBUG,"codemao","探月少儿编程App")
    }
}