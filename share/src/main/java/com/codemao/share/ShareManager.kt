package com.codemao.share

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.annotation.DrawableRes
import java.io.*
import java.lang.StringBuilder

object ShareManager {
    var QQ_APP_ID: String= ""
    var WX_APP_ID: String = ""
    var shareDrawableId: Int = R.drawable.share_logo
    private const val LISTENER_WX_RESP: String = "LISTENER_WX_RESP"
    private lateinit var context:Application
    private lateinit var callBack: ShareCallBack
     fun getContext(): Context {
        return context
    }

    fun loadBitemap(minProgramCover: String, function: (bitmap:Bitmap) -> Unit){
        callBack.loadBitemap(minProgramCover,function)
    }

    fun getPermission(context: Context?, permissions: Array<String>, function: (granted:Boolean) -> Unit) {
        callBack.getPermission(context,permissions,function)
    }

    fun init(context:Application,wxId:String,qqId:String,@DrawableRes shareDrawableId:Int,callBack: ShareCallBack){
        ShareManager.callBack = callBack
        QQ_APP_ID = qqId
        WX_APP_ID = wxId
        ShareManager.context = context
        this.shareDrawableId = shareDrawableId
    }

    var broadcastReceiver: BroadcastReceiver? = null
    fun listeneWxResp(context: Context?) {
        if (context == null) return
        unregisterLisntener(context)
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // SendAuth.Resp resp = new SendAuth.Resp();
                context.unregisterReceiver(this)
                broadcastReceiver = null
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show()
            }
        }
        context.applicationContext.registerReceiver(
            broadcastReceiver,
            IntentFilter(LISTENER_WX_RESP)
        )
    }

    fun unregisterLisntener(context: Context?) {
        if (context != null && broadcastReceiver != null) {
            context.applicationContext.unregisterReceiver(broadcastReceiver)
        }
    }


    fun encodeBase64File(isAssets: Boolean, path: String): String {
        val stream: InputStream = if (isAssets) {
            getContext().assets.open(path)
        } else {
            FileInputStream(File(path))
        }
        val br = BufferedReader(InputStreamReader(stream))
        val sb = StringBuilder()
        var s: String?
        while (br.readLine().also { s = it } != null) {
            sb.append(s)
        }
        br.close()
        return sb.toString()
    }

    fun base64ToBitmap(base64Data: String?): Bitmap? {
        val split = base64Data?.split(",")
        if (split == null || split.size <= 1) {
            return null
        }
        val bytes: ByteArray = Base64.decode(split?.get(1), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun Bitmap2Bytes(bm: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 80, baos)
        return baos.toByteArray()
    }
}