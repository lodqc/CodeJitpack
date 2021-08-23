package com.codemao.healthmanageui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import com.codemao.healthmanager.HealthManager

class HealthManagerLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_manager_log)
        val tvLog = findViewById<TextView>(R.id.tv_log)
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }
        tvLog.movementMethod = ScrollingMovementMethod.getInstance();
        HealthManager.getLog().forEach {
            tvLog.append(it + "\n\n")
        }
    }
}