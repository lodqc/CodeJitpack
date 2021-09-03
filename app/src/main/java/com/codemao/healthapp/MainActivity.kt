package com.codemao.healthapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.codemao.healthmanager.HealthManager
import com.codemao.share.showNativePop
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    companion object{
        lateinit var activity:MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
//        HealthManage.init(MyApplication.application, object : HealthInterFace {
//            override fun getActivity(): AppCompatActivity {
//                return this@MainActivity
//            }
//        })
//        SensorsHelper.trackAppInstallWithDialog(this)
//        for(bean in HealthManager.getLog()) {
//            Log.e("fq", "onCreate: $bean")
//        }
//        /pages/webview/main?target=https%3A%2F%2Ftest-b2c-school.codemao.cn%2Ftanyue%2Fdemo-landing-school%3FcourseId%3D1564%26termId%3D2839%26linkId%3D1161%26stepId%3D5fbbb7f92602ed5382480530%26userId%3D168653436%26workId%3D3528198%26type%3Dnemo
//        showShareTextPop(this,text= "https://test-activity-h5.codemao.cn/code-activity-works-detail-view?activityId=1&activityWorkId=24&shareUserId=1530259880", title = "新的作品",uri = Uri.parse("https://dev-static.codemao.cn/nemo/rkdiOalZF.cover"))
        val fromJson = Gson().fromJson("{\n" +
                "    \"payload\": {\n" +
                "        \"type\": 3,\n" +
                "        \"title\": \"有个有趣的活动等你来参加\",\n" +
                "        \"url\": \"https://test-activity-h5.codemao.cn/code-activity-works-detail-view?activityId=1&activityWorkId=2&shareUserId=1428401047\",\n" +
                "        \"image_url\": \"https://dev-static.codemao.cn/nemo/SylCoRYBa_.cover\",\n" +
                "        \"desc\": \"我在编程猫上看到了有个作品\"\n" +
                "    }\n" +
                "}", ShareNativeBean::class.java)
        fromJson?.payload?.run {
            showNativePop(this@MainActivity,type,title,url,"",imageBase64,desc)
        }
    }
}