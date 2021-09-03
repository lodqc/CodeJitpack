package com.codemao.healthapp

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.codemao.healthmanageui.HealthManagerUi
import com.codemao.sensors.SensorsHelper
import com.codemao.share.ShareCallBack
import com.codemao.share.ShareManager
import com.codemao.share.WxShareUtil

class MyApplication : Application(){
     val WX_APPID = "wx41c6a4b2161e6f6e"
     val QQ_APPID2 = "1112008780"
    override fun onCreate() {
        super.onCreate()
        SensorsHelper.init(this,BuildConfig.DEBUG,"codemao","探月少儿编程App")
        HealthManagerUi.init(this,BuildConfig.DEBUG,MainActivity::class.java)
        ShareManager.init(this, WX_APPID,"gh_c3ea31bd2f63",QQ_APPID2,R.drawable.ic_launcher,object : ShareCallBack {
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
                            val bitmap = (drawable as BitmapDrawable).toBitmap()
//                            val bitmap2Bytes = WxShareUtil.bitmap2Bytes(drawableToBitmap, 128)

                            MainActivity.activity.findViewById<ImageView>(R.id.ivView).setImageBitmap(bitmap)
                            function.invoke(bitmap)
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
}