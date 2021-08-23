package com.codemao.healthapp

import android.app.Activity
import android.app.Application
import android.text.TextUtils
import com.codemao.healthmanager.HealthManager
import com.codemao.healthmanager.config.HMConfig
import com.codemao.healthmanageui.HealManagerUi
import com.codemao.sensors.SensorsHelper
import java.lang.Exception

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        SensorsHelper.init(this,BuildConfig.DEBUG,"codemao","探月少儿编程App")
        HealManagerUi.init(this,BuildConfig.DEBUG,MainActivity::class.java)
    }
}