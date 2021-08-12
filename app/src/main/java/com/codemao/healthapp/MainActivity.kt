package com.codemao.healthapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codemao.healthmanage.HealthInterFace
import com.codemao.healthmanage.HealthManage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HealthManage.init(MyApplication.application, object : HealthInterFace {
            override fun getActivity(): AppCompatActivity {
                return this@MainActivity
            }
        })
    }
}