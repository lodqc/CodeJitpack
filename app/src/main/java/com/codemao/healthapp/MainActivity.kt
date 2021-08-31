package com.codemao.healthapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.codemao.healthmanager.HealthManager

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
        for(bean in HealthManager.getLog()) {
            Log.e("fq", "onCreate: $bean")
        }
    }
}