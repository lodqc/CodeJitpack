package com.codemao.healthmanage

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.work.*
import com.codemao.healthmanage.HealthManage.healthInterFace
import com.lxj.xpopup.XPopup
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
object HealthManage : LifecycleObserver {
    lateinit var healthInterFace:HealthInterFace
    var current = System.currentTimeMillis()

    fun init(context: Application, interFace: HealthInterFace) {
        healthInterFace = interFace
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        //需求一，45分钟弹出提示弹窗
        //最短时间间隔为15分钟
        val worker = PeriodicWorkRequestBuilder<HealthDialogShowWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                HealthManage::class.java.name,
                ExistingPeriodicWorkPolicy.KEEP,
                worker
            )

        //注册时间改变广播
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        }
        context.registerReceiver(TimeTickReceiver(), filter)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.e("fq", "Lifecycle.Event.ON_RESUME")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.e("fq", "Lifecycle.Event.ON_STOP")
        current = System.currentTimeMillis()
    }

    /**
     * 判断用户的设备时区是否为东八区（中国） 2014年7月31日
     * @return
     */
    fun isInEasternEightZones(): Boolean {
        return TimeZone.getDefault() === TimeZone.getTimeZone("GMT+08")
    }

}


class HealthDialogShowWorker(appContext: Context,workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        healthInterFace.getActivity().run {
            runOnUiThread {
                XPopup.Builder(this).asConfirm(
                    "我是标题", "我是内容"
                ) {
                    Toast.makeText(healthInterFace.getActivity(), "123", Toast.LENGTH_SHORT).show()
                }
                    .show()
            }
        }
        return Result.success()
    }
}
