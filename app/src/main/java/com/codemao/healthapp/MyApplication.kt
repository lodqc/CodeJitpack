package com.codemao.healthapp

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.codemao.healthmanageui.HealthManagerUi
import com.codemao.sensors.SensorsHelper
import com.codemao.share.ShareCallBack
import com.codemao.share.ShareManager

class MyApplication : Application(){
     val WX_APPID = "wx41c6a4b2161e6f6e"
     val QQ_APPID2 = "1112008780"
    override fun onCreate() {
        super.onCreate()
        SensorsHelper.init(this,BuildConfig.DEBUG,"codemao","探月少儿编程App")
        HealthManagerUi.init(this,BuildConfig.DEBUG,MainActivity::class.java)
        ShareManager.init(this, WX_APPID,QQ_APPID2,object : ShareCallBack {
            override fun getPermission(
                context: Context?,
                permissions: Array<String>,
                function: (granted: Boolean) -> Unit
            ) {
            }

            override fun loadBitemap(minProgramCover: String): Bitmap {
                TODO("Not yet implemented")
            }
        })
    }
}