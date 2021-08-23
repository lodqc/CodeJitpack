package com.codemao.healthmanageui

import android.app.Application
import com.codemao.healthmanager.HealthManager
import com.codemao.healthmanager.config.HMConfig

object HealManagerUi {
    /**
     * @clazz 设置欢迎页面
     */
    fun init(application: Application, isDebug: Boolean, clazz: Class<*>) {
        val startHour = 21
        val startMinute = 0
        val startSecond = 0
        val endHour = 6
        val endMinute = 0
        val endSecond = 0
        val overDay = 1
        val appUseLimit = 1800
        val backgroundTime = 300
        //健康管理
        val hmConfig: HMConfig =
            HMConfig.Companion.createConfig()
                //设置是否是debug
                .isDebug(isDebug)
                //设置能处于后台的最大时间单位秒，超过这个时间，将会重新开始计时
                .setMaxInBackgroundTime(backgroundTime)
                //设置欢迎页面
                .setAppSplashActivity(clazz)
                //设置深夜模式信息
                .setNightModeInfo(
                    startHour,
                    startMinute,
                    startSecond,
                    endHour,
                    endMinute,
                    endSecond,
                    overDay == 1
                )
                //设置最大可以学习的时间 单位秒
                .setStudyTimeLimit(appUseLimit)
                //设置浮层的ui布局
                .setLayoutCover(R.layout.hmu_activity_health_manager_cover)
                //设置弹窗的ui布局
                .setLayoutDialog(R.layout.hmu_activity_health_manager_dialog)
        //初始化
        HealthManager.init(
            application,
            hmConfig
        )
    }
}