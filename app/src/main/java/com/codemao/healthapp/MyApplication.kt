package com.codemao.healthapp

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import coil.ImageLoader
import coil.request.ImageRequest
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
            override fun loadBitemap(minProgramCover: String, function: (bitmap: Bitmap) -> Unit) {
                val imageLoader = ImageLoader.Builder(this@MyApplication)
                    .availableMemoryPercentage(0.25)
                    .crossfade(true)
                    .build()
                imageLoader.enqueue(
                    ImageRequest.Builder(this@MyApplication)
                    .data(minProgramCover)
                    .target(
                        onSuccess = { drawable ->
                            function.invoke(
                                drawableToBitmap(drawable))
                        }
                    )
                    .build())
            }

            override fun getPermission(
                context: Context?,
                permissions: Array<String>,
                function: (granted: Boolean) -> Unit
            ) {
            }
        })
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
}