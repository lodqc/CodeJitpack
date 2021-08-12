package com.codemao.healthmanage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.codemao.healthmanage.HealthManage.healthInterFace
import com.lxj.xpopup.XPopup
import java.util.*

class TimeTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        Log.e("Fq", "hour$hour$minute")
//        //需求二，九点之后弹出提示弹窗
//        XPopup.Builder(healthInterFace.getActivity()).asConfirm(
//            "我是标题", "九点之后弹出提示弹窗"
//        ) {
//            Toast.makeText(context, "123", Toast.LENGTH_SHORT).show()
//        }
//            .show()
    }
}