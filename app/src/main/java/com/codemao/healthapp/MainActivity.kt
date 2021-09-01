package com.codemao.healthapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.codemao.healthmanager.HealthManager
import com.codemao.share.showShareTextPop

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
        showShareTextPop(this,text= "https://test-b2c-school.codemao.cn/tanyue/demo-landing-school?courseId=1564&termId=2839&linkId=1161&stepId=5fbbb7f92602ed5382480530&userId=168653436&workId=3528198&type=nemo", title = "点击试玩小训练师的编程作品",uri = Uri.parse("https://dev-static.codemao.cn/nemo/rkdiOalZF.cover"))
    }
}