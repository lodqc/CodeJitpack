package com.codemao.healthapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codemao.sensors.SensorsHelper
import com.codemao.sensors.SensorsInterFace

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        HealthManage.init(MyApplication.application, object : HealthInterFace {
//            override fun getActivity(): AppCompatActivity {
//                return this@MainActivity
//            }
//        })
        SensorsHelper.init(MyApplication.application,BuildConfig.DEBUG,"codemao","探月少儿编程App",object :SensorsInterFace{
            override fun getActivity(): Activity {
                return this@MainActivity
            }
        })
    }
}