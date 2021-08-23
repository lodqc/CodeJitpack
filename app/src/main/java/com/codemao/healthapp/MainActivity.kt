package com.codemao.healthapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.codemao.healthmanageui.HealthManagerLogActivity
import com.codemao.sensors.SensorsHelper
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        HealthManage.init(MyApplication.application, object : HealthInterFace {
//            override fun getActivity(): AppCompatActivity {
//                return this@MainActivity
//            }
//        })
//        SensorsHelper.trackAppInstallWithDialog(this)
        startActivity(Intent(this, HealthManagerLogActivity::class.java))
    }
}