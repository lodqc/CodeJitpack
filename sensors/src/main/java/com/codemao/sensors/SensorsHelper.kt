package com.codemao.sensors

import android.Manifest
import android.app.Activity
import android.app.Application
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.codemao.sensors.PopUtil.checkSensorsPermissionDialog
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


object SensorsHelper {
    lateinit var context: Application
    lateinit var channel: String
    lateinit var productName: String

    @JvmStatic
    fun init(
        context: Application,
        isDebug: Boolean,
        channel: String,
        productName: String
    ) {
        this.context = context
        this.channel = channel
        this.productName = productName
        val SA_SERVER_URL =
            "https://shence-data.codemao.biz/sa?project=${if (isDebug) "default" else "production"}"

        // 初始化配置
        val saConfigOptions = SAConfigOptions(SA_SERVER_URL)
        // 开启全埋点
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(
            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
                    SensorsAnalyticsAutoTrackEventType.APP_START or
                    SensorsAnalyticsAutoTrackEventType.APP_END
//                    or SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
        )
            //开启 Log
            .enableLog(true)
            // 开启 App 打通 H5
            //X5 内核打通，在初始化后添加 SensorsDataAPI.sharedInstance().showUpX5WebView(WebView,true);
            .enableJavaScriptBridge(true)
            // 开启可视化全埋点
            .enableVisualizedAutoTrack(true)
            //开启屏幕方向的自动采集
            .enableTrackScreenOrientation(true)
            // 传入 true 代表开启推送点击事件自动采集
            .enableTrackPush(true)
            //开启点击分析功能
            .enableHeatMap(true)
            //利用初始化的 SAConfigOptions 对象开启 crash 信息采集
            .enableTrackAppCrash()
        // 需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions(context, saConfigOptions)
//        // 初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
//        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
//        //设置经纬度
//        SensorsDataAPI.sharedInstance().setGPSLocation(latitude,longitude);
        // 初始化 SDK 后，设置动态公共属性
        registerSuperProperties()
    }


    /**
     * 设置事件公共属性
     */
    private fun registerSuperProperties() {
        // 将应用名称作为事件公共属性，后续所有 track() 追踪的事件都会自动带上 "product_name" 属性
        try {
            val properties = JSONObject()
            properties.put("product_name", productName)
            properties.put("source", channel)
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * 登录方法确定是否是后端上报
     */
    fun login(
        pid: String,
        userId: String,
        nickname: String,
        birthday: String,
        sex: String,
        fullname: String
    ) {
        SensorsDataAPI.sharedInstance().login(userId)
        val properties = JSONObject()
        properties.put("nickname", nickname)
        properties.put("birthday", birthday)
        properties.put("sex", sex)
        properties.put("fullname", fullname)
        properties.put("pid", pid)
        // 设定用户属性
        SensorsDataAPI.sharedInstance().profileSet(properties)
    }


    /**
     * 记录激活事件弹出权限请求弹窗
     */
    fun trackAppInstallWithDialog(activity: Activity) {
        if (!SensorsSPUtil.getInstance().getBoolean(Manifest.permission.READ_PHONE_STATE)&&checkSensorsPermissionDialog(
                activity
            ) {
                trackAppInstall()
            }
        ) {
            trackAppInstall()
        } else {
            trackAppInstall()
        }
    }
    /**
     * 记录激活事件
     */
    fun trackAppInstall() {
        if(!SensorsSPUtil.getInstance().getBoolean("isDownloadChannel")){
            try {
                val properties = JSONObject()
                //这里的 DownloadChannel 负责记录下载商店的渠道，值应传入具体应用商店包的标记。如果没有为不同商店打多渠道包，则可以忽略该属性的代码示例。
                properties.put("DownloadChannel", channel)
                // 触发激活事件
                // 如果您之前使用 trackInstallation() 触发的激活事件，需要继续保持原来的调用，无需改为 trackAppInstall()，否则会导致激活事件数据分离。
                SensorsDataAPI.sharedInstance().trackAppInstall(properties)
                SensorsSPUtil.getInstance().put("isDownloadChannel",true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @JvmStatic
    fun showUpX5WebView(web: WebView) {
        SensorsDataAPI.sharedInstance().showUpX5WebView(web, true);
    }


    //触发 Activity 的浏览页面事件
    fun trackViewScreen(activity: Activity) {
        SensorsDataAPI.sharedInstance().trackViewScreen(activity)
    }

    //触发 Fragment 的浏览页面事件
    fun trackViewScreen(fragment: Fragment) {
        SensorsDataAPI.sharedInstance().trackViewScreen(fragment)
    }

    //自定义埋点上报
    fun report(eventName:String,json:JSONObject){
        SensorsDataAPI.sharedInstance().track(eventName, json)
    }

}