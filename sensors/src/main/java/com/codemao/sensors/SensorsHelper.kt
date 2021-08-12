package com.codemao.sensors

import android.app.Application
import android.webkit.WebView
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType
import com.sensorsdata.analytics.android.sdk.SAConfigOptions
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception


object SensorsHelper {
    lateinit var context: Application
    lateinit var channel:String
    lateinit var productName:String

    @JvmStatic
    fun init(context: Application,channel:String,productName:String) {
        this.context = context
        this.channel = channel
        this.productName = productName
        val SA_SERVER_URL = "数据接收地址"

        // 初始化配置

        // 初始化配置
        val saConfigOptions = SAConfigOptions(SA_SERVER_URL)
        // 开启全埋点
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(
            SensorsAnalyticsAutoTrackEventType.APP_CLICK or
                    SensorsAnalyticsAutoTrackEventType.APP_START or
                    SensorsAnalyticsAutoTrackEventType.APP_END or
                    SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN
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
        // 初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();
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
    fun login(pid:String,userId:String,nickname:String,birthday:String,sex:String,fullname:String) {
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
     * 记录激活事件
     */
    fun trackAppInstall() {
        try {
            val properties = JSONObject()
            //这里的 DownloadChannel 负责记录下载商店的渠道，值应传入具体应用商店包的标记。如果没有为不同商店打多渠道包，则可以忽略该属性的代码示例。
            properties.put("DownloadChannel", channel)
            // 触发激活事件
            // 如果您之前使用 trackInstallation() 触发的激活事件，需要继续保持原来的调用，无需改为 trackAppInstall()，否则会导致激活事件数据分离。
            SensorsDataAPI.sharedInstance().trackAppInstall(properties)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @JvmStatic
    fun showUpX5WebView(web: WebView) {
        SensorsDataAPI.sharedInstance().showUpX5WebView(web, true);
    }

    /**
     * 运营位点击
     * @param page_name 页面名称 首页，探索
     * @param mkt_type 运营位类型 开屏/弹窗/轮播位
     * @param mkt_id 运营位id
     * @param mkt_name 运营位标题
     * @param content_id 内容id
     * @param content_name 内容标题
     * @param click_type 点击类型 关闭/点击进入详情
     * @param added_time 上架时间
     */
    fun mktClick(
        page_name: String,
        mkt_type: String,
        mkt_id: String,
        mkt_name: String,
        content_id: String,
        content_name: String,
        click_type: String,
        added_time: String,
    ) {
        val properties = JSONObject()
        properties.put("page_name", page_name)
        properties.put("mkt_type", mkt_type)
        properties.put("mkt_id", mkt_id)
        properties.put("mkt_name", mkt_name)
        properties.put("content_id", content_id)
        properties.put("content_name", content_name)
        properties.put("click_type", click_type)
        properties.put("added_time", added_time)
        SensorsDataAPI.sharedInstance().track("MktClick", properties)
    }

    /**
     * 进入课程详情
     * @param term_id 课期id
     * @param package_id 课包id
     * @param package_name 课包名称
     * @param chapter_sort 章节序号
     * @param chapter_name 章节名称
     * @param course_id 课程id
     * @param course_sort 课程序号
     * @param course_name 课程名称
     * @param course_form 课程形态
     * @param start_time 开课时间
     */
    fun courseVisit(
        term_id: String,
        package_id: String,
        package_name: String,
        chapter_sort: String,
        chapter_name: String,
        course_id: String,
        course_sort: String,
        course_name: String,
        course_form: String,
        start_time: String,
    ) {
        val properties = JSONObject()
        properties.put("term_id", term_id)
        properties.put("package_id", package_id)
        properties.put("package_name", package_name)
        properties.put("chapter_sort", chapter_sort)
        properties.put("chapter_name", chapter_name)
        properties.put("course_id", course_id)
        properties.put("course_sort", course_sort)
        properties.put("course_name", course_name)
        properties.put("course_form", course_form)
        properties.put("start_time", start_time)
        SensorsDataAPI.sharedInstance().track("CourseVisit", properties)
    }

    /**
     * 进入课程环节
     * @param course_id 课程id
     * @param link_id 环节id
     * @param link_name 环节名称
     */
    fun courseVisit(
        course_id: String,
        link_id: String,
        link_name: String,
    ) {
        val properties = JSONObject()
        properties.put("course_id", course_id)
        properties.put("link_id", link_id)
        properties.put("link_name", link_name)
        SensorsDataAPI.sharedInstance().track("CourseVisit", properties)
    }

    /**
     * 进入课程步骤
     * @param course_id 课程id
     * @param link_id 环节id
     * @param step_id 步骤id
     * @param step_type 步骤类型
     * @param step_name 步骤名称
     */
    fun courseStepVisit(
        course_id: String,
        link_id: String,
        step_id: String,
        step_type: String,
        step_name: String,
    ) {
        val properties = JSONObject()
        properties.put("course_id", course_id)
        properties.put("link_id", link_id)
        properties.put("step_id", step_id)
        properties.put("step_type", step_type)
        properties.put("step_name", step_name)
        SensorsDataAPI.sharedInstance().track("CourseStepVisit", properties)
    }

    /**
     * 离开教室
     * @param course_id 课程id
     * @param link_id 环节id
     * @param step_id 步骤id
     * @param step_name 步骤名称
     * @param valid_time 有效观看时长
     */
    fun courseExit(
        course_id: String,
        link_id: String,
        step_id: String,
        step_name: String,
        valid_time: String,
    ) {
        val properties = JSONObject()
        properties.put("course_id", course_id)
        properties.put("link_id", link_id)
        properties.put("step_id", step_id)
        properties.put("step_name", step_name)
        properties.put("valid_time", valid_time)
        SensorsDataAPI.sharedInstance().track("CourseExit", properties)
    }

    /**
     * 作业分享
     * @param package_id 课包id
     * @param term_id 课期id
     * @param course_id 课程id
     * @param share_method 分享方式
     */
    fun homeworkShare(
        package_id: String,
        term_id: String,
        course_id: String,
        share_method: String,
    ) {
        val properties = JSONObject()
        properties.put("package_id", package_id)
        properties.put("term_id", term_id)
        properties.put("course_id", course_id)
        properties.put("share_method", share_method)
        SensorsDataAPI.sharedInstance().track("HomeworkShare", properties)
    }

    /**
     * 视频分享
     * @param package_id 课包id
     * @param term_id 课期id
     * @param course_id 课程id
     * @param share_method 分享方式
     */
    fun homeworkVideoShare(
        package_id: String,
        term_id: String,
        course_id: String,
        share_method: String,
    ) {
        val properties = JSONObject()
        properties.put("package_id", package_id)
        properties.put("term_id", term_id)
        properties.put("course_id", course_id)
        properties.put("share_method", share_method)
        SensorsDataAPI.sharedInstance().track("HomeworkVideoShare", properties)
    }

    /**
     * 运行作品
     * @param work_id_only 作品唯一id
     */
    fun workRun(
        work_id_only: String,
    ) {
        val properties = JSONObject()
        properties.put("work_id_only", work_id_only)
        SensorsDataAPI.sharedInstance().track("WorkRun", properties)
    }

    /**
     * 预览作品
     * @param work_id_only 作品唯一id
     */
    fun workPlay(
        work_id_only: String,
    ) {
        val properties = JSONObject()
        properties.put("work_id_only", work_id_only)
        SensorsDataAPI.sharedInstance().track("WorkPlay", properties)
    }

    /**
     * 查看内容
     * @param page_from 上级页面
     * @param column_id 栏目id
     * @param column_title 栏目标题
     * @param content_id 内容id
     * @param content_title 内容标题
     */
    fun homeworkVideoShare(
        page_from: String,
        column_id: String,
        column_title: String,
        content_id: String,
        content_title: String,
    ) {
        val properties = JSONObject()
        properties.put("page_from", page_from)
        properties.put("column_id", column_id)
        properties.put("column_title", column_title)
        properties.put("content_id", content_id)
        properties.put("content_title", content_title)
        SensorsDataAPI.sharedInstance().track("HomeworkVideoShare", properties)
    }
}