package com.codemao.share

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import cn.codemao.android.account.util.WechatManager
import com.codemao.share.ShareManager.encodeBase64File
import com.codemao.share.ShareUtil.Companion.CREATE_BITMAP_FAILED
import com.codemao.share.ShareUtil.Companion.UNINSTALL_WX
import cn.codemao.android.share.interfaces.IshareResult
import com.codemao.share.NewWxShareUtil.api
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.*

/**
 * <pre>
 *     author : yangliu
 *     e-mail : yangliu@codemao.cn
 *     time   : 2020/12/02
 *     desc   :
 * </pre>
 */
object WxShareUtil {

    private var sharing = false


    fun shareImgToWXMoment(context: Context, localImageUrl: String?, wxShareListener: IshareResult?) {
        shareImgToWX(context, localImageUrl,wxShareListener, SendMessageToWX.Req.WXSceneTimeline)
    }

    fun shareImgToWXFriend(context: Context, localImageUrl: String?, wxShareListener: IshareResult?) {
        shareImgToWX(context, localImageUrl, wxShareListener, SendMessageToWX.Req.WXSceneSession)
    }

    /**
     * @param context
     * @param localImageUrl 要分享图片的本地地址，必须在fileprovider规定的文件目录下
     */
    private fun shareImgToWX(context: Context, localImageUrl: String?, wxShareListener: IshareResult?, scene: Int) {
        WechatManager.getInstance().ishareResult = wxShareListener
        if (!api.isWXAppInstalled) {
            wxShareListener?.onFailure(UNINSTALL_WX)
            return
        }
        //创建一个WXImageObject对象，用于封装要发送的图片
        val imageObject = WXImageObject()
        //兼容android11版本
        if (checkVersionValid(api) && checkAndroidNotBelowN()) {
            imageObject.setImagePath(getFileUri(context, localImageUrl))
        } else {
            imageObject.setImagePath(localImageUrl)
        }

        //创建一个WXMediaMessage对象
        val msg = WXMediaMessage()
        msg.mediaObject = imageObject
        val req = SendMessageToWX.Req()
        //transaction字段用于唯一标识一个请求，这个必须有，否则会出错
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg

        //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
        // req.scene = SendMessageToWX.Req.WXSceneSession;
        req.scene = scene
        api.sendReq(req)
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    fun shareBitmapToWXMoment(context: Context?, bitmap: Bitmap?, wxShareListener: IshareResult?) {
        if (context == null || bitmap == null || sharing) return
        sharing = true
        bitmapSaveFile(context, bitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    sharing = false
                    shareImgToWXMoment(context, file?.absolutePath, wxShareListener)
                }, {
                    wxShareListener?.onFailure(CREATE_BITMAP_FAILED)
                    sharing = false
                })
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    fun shareBitmapToWXFriend(context: Context?, bitmap: Bitmap?,wxShareListener: IshareResult?) {
        if (context == null || bitmap == null || sharing) return
        sharing = true
        bitmapSaveFile(context, bitmap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    sharing = false
                    shareImgToWXFriend(context, file?.absolutePath, wxShareListener)
                }, {
                    wxShareListener?.onFailure(CREATE_BITMAP_FAILED)
                    sharing = false
                })
    }


    /**
     * 注意，先清空文件夹文件
     *
     * @return
     */
    fun bitmapSaveFile(context: Context,
                       bitmap: Bitmap): Observable<File?> {
        return Observable.create<File?> { emitter ->
            var outputStream: OutputStream? = null
            try {
                val fileName = "img_" + System.currentTimeMillis() + ".jpg"
                val screenshotsDir = File(context.getExternalFilesDir(null), "shareData")
                if (!screenshotsDir.exists()) { //如果该文件夹不存在，则进行创建
                    screenshotsDir.mkdirs() //创建文件夹
                } else {
                    for (file in screenshotsDir.listFiles()) {
                        if (file.isFile) {
                            file.delete() // 删除所有文件
                        }
                    }
                }
                val screenshotFile = File(screenshotsDir, fileName)
                outputStream = BufferedOutputStream(FileOutputStream(screenshotFile))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                outputStream.flush()
                emitter.onNext(screenshotFile)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (ignored: IOException) {
                        //Log.e(TAG, "Failed to close OutputStream.");
                    }
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    /**
     * 注意，先清空文件夹文件
     *
     * @return
     */
    fun bitmapSaveSDCardFile(context: Context,
                             bitmap: Bitmap): Observable<File?> {
        return Observable.create<File?> { emitter ->
            var outputStream: OutputStream? = null
            try {

                val fileName = "img_" + System.currentTimeMillis() + ".jpg"
                val screenshotsDir = File(Environment.getExternalStorageDirectory().toString() + "/codemao/", "shareData")
                if (!screenshotsDir.exists()) { //如果该文件夹不存在，则进行创建
                    screenshotsDir.mkdirs() //创建文件夹
                } else {
                    for (file in screenshotsDir.listFiles()) {
                        if (file.isFile) {
                            file.delete() // 删除所有文件
                        }
                    }
                }
                val screenshotFile = File(screenshotsDir, fileName)
                outputStream = BufferedOutputStream(FileOutputStream(screenshotFile))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                outputStream.flush()
                emitter.onNext(screenshotFile)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                        bitmap.recycle()

                    } catch (ignored: IOException) {
                        //Log.e(TAG, "Failed to close OutputStream.");
                    }
                }

            }
        }.subscribeOn(Schedulers.io())
    }

    // 判断微信版本是否为7.0.13及以上
    private fun checkVersionValid(api: IWXAPI): Boolean {
        return api.wxAppSupportAPI >= 0x27000D00
    }

    // 判断Android版本是否11 及以上
    private fun checkAndroidNotBelowN(): Boolean {
        return Build.VERSION.SDK_INT >= 30
    }

    private fun getFileUri(context: Context, filePath: String?): String? {
        return getFileUri(context, File(filePath))
    }

    private fun getFileUri(context: Context, file: File?): String? {
        if (file == null || !file.exists()) {
            return null
        }
        val contentUri = FileProvider.getUriForFile(context,
                context.packageName + ".wechatShare",  // 要与`AndroidManifest.xml`里配置的`authorities`一致，假设你的应用包名为com.example.app
                file)

        // 授权给微信访问路径
        context.grantUriPermission("com.tencent.mm",  // 这里填微信包名
                contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return contentUri.toString() // contentUri.toString() 即是以"content://"开头的用于共享的路径
    }

    fun shareMiniProgram(username: String, url: String, path: String, desc: String, title: String, context: Context,bitmap: Bitmap? = null) {
        var minProgramCover = ""
        if (url.startsWith("http")){
            minProgramCover = url
        }else if (url.startsWith("/data")){
            minProgramCover = encodeBase64File(false, url)
        }
        kotlin.runCatching {
            ShareManager.loadBitemap(minProgramCover){
                val miniProgramObj = WXMiniProgramObject()
                miniProgramObj.webpageUrl = "http://www.qq.com" // 兼容低版本的网页链接
                miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE // 正式版:0，测试版:1，体验版:2
                miniProgramObj.userName = username // 小程序原始id
                miniProgramObj.path = path //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
                miniProgramObj.withShareTicket = true
                val msg = WXMediaMessage(miniProgramObj)
                msg.title = title // 小程序消息title
                msg.description = desc
                if (bitmap != null){
                    msg.thumbData = bitmap2Bytes(bitmap, 128)
                }else{
                    msg.thumbData = bitmap2Bytes(it, 128)
                }
                val req: SendMessageToWX.Req = SendMessageToWX.Req()
                req.transaction = System.currentTimeMillis().toString()
                req.message = msg
                req.scene = SendMessageToWX.Req.WXSceneSession // 目前只支持会话
                WXAPIFactory.createWXAPI(context, ShareManager.WX_APP_ID).sendReq(req)
            }
        }.onFailure {
            Log.d("111", "作品封面加载异常")
        }

    }


    fun bitmap2Bytes(bitmap: Bitmap, maxSize: Int): ByteArray? {
        try {

            var bit = bitmap

            val out = ByteArrayOutputStream()
            var quality = 100

            val width = bitmap.width * 1f
            val height = bitmap.height * 1f

            if (height != 0f || width != 0f) {
                if (width / height > 5f / 4) {//宽高比大于5:4
                    val finalW = height * 5 / 4
                    val startX = (width - finalW) / 2
                    if (finalW < width && startX >= 0 && (startX + finalW) <= width) {
                        bit = Bitmap.createBitmap(bitmap, startX.toInt(), 0, finalW.toInt(), height.toInt())
                    }

                } else {//小于5:4  强行改成5:4

                    val finalH = width * 4 / 5
                    val startY = (height - finalH) / 2

                    if (finalH < height && startY >= 0 && (startY + finalH) <= finalH) {
                        bit = Bitmap.createBitmap(bitmap, 0, startY.toInt(), width.toInt(), finalH.toInt())
                    }

                }
            }


            bit.compress(Bitmap.CompressFormat.JPEG, quality, out)
            var bytes = out.toByteArray()
            while (bytes.size > maxSize * 1024) {
                quality -= 10
                out.reset()
                bit.compress(Bitmap.CompressFormat.JPEG, quality, out)
                bytes = out.toByteArray()
            }



            return bytes
        } catch (e: java.lang.Exception) {
            Log.e("ImageCompressUtil", "QualityCompress$e")
        }
        return null
    }

}