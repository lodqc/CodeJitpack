package com.codemao.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.WXAPIFactory


object NewWxShareUtil {
    val api = WXAPIFactory.createWXAPI(ShareManager.getContext(), ShareManager.WX_APP_ID)
    fun shareText(text: String, mTargetScene: Int) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        val textObj = WXTextObject()
        textObj.text = text

        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象

        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage()
        msg.mediaObject = textObj
        msg.description = text

        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = mTargetScene
        //调用api接口，发送数据到微信
        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    fun shareUrl(
        webpageUrl: String,
        title: String,
        description: String,
        mTargetScene: Int,
        imgUrl: String?,
        imgBase64: String?
    ) {
        if (TextUtils.isEmpty(imgUrl) && TextUtils.isEmpty(imgBase64)) {
            val decodeResource = BitmapFactory.decodeResource(
                ShareManager.getContext().resources,
                ShareManager.shareDrawableId
            )
            shareUrl(webpageUrl, title, description, mTargetScene, decodeResource)
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            imgUrl?.let {
                ShareManager.loadBitemap(it) {
                    shareUrl(webpageUrl, title, description, mTargetScene, it)
                }
            }
        }
        if (!TextUtils.isEmpty(imgBase64)) {
            ShareManager.base64ToBitmap(imgBase64)?.run {
                shareUrl(webpageUrl, title, description, mTargetScene, this)
            }
        }
    }

    fun shareUrl(
        webpageUrl: String,
        title: String,
        description: String,
        mTargetScene: Int,
        bitmap: Bitmap
    ) {
        //初始化一个WXWebpageObject，填写url
        //初始化一个WXWebpageObject，填写url
        val webpage = WXWebpageObject()
        webpage.webpageUrl = webpageUrl

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage(webpage)
        msg.title = title
        msg.description = description
        msg.thumbData = WxShareUtil.bitmap2Bytes(bitmap, 128)
        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = mTargetScene
        //        req.userOpenId = getOpenId()
        //调用api接口，发送数据到微信
        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * @param username 小程序原始id
     * @param path 小程序路径
     * @param title 标题
     * @param desc 描述
     * @param imgUrl 图片url和图片Base64二选一
     * @param imgBase64 图片Base64和图片url二选一
     * @param webpage 兼容的跳转网页，低版本兼容
     * 没有imgUrl和imgBase64使用应用图标，作为封面
     */
    fun shareMiniProgram(
        username: String,
        path: String,
        title: String,
        desc: String,
        imgUrl: String? = null,
        imgBase64: String? = null,
        webpage: String? = null
    ) {
        if (TextUtils.isEmpty(imgUrl) && TextUtils.isEmpty(imgBase64)) {
            val decodeResource = BitmapFactory.decodeResource(
                ShareManager.getContext().resources,
                ShareManager.shareDrawableId
            )
            if(decodeResource != null){
                shareMiniProgram(username, path, title, desc, decodeResource,webpage)
            }
        }
        if (!TextUtils.isEmpty(imgUrl)) {
            imgUrl?.let {
                ShareManager.loadBitemap(it) { it1 ->
                    shareMiniProgram(username, path, title, desc, it1,webpage)
                }
            }
        }
        if (!TextUtils.isEmpty(imgBase64)) {
            ShareManager.base64ToBitmap(imgBase64)?.run {
                shareMiniProgram(username, path, title, desc, this,webpage)
            }
        }
    }


    fun shareMiniProgram(
        username: String,
        path: String,
        title: String,
        desc: String,
        bitmap: Bitmap,
        webpage: String? = "http://www.qq.com"
    ) {
        val miniProgramObj = WXMiniProgramObject()
        miniProgramObj.webpageUrl = webpage  // 兼容低版本的网页链接
        miniProgramObj.miniprogramType =
            WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE // 正式版:0，测试版:1，体验版:2
        miniProgramObj.userName = username // 小程序原始id
        miniProgramObj.path = path //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
        miniProgramObj.withShareTicket = true
        val msg = WXMediaMessage(miniProgramObj)
        msg.title = title // 小程序消息title
        msg.description = desc
        msg.thumbData = WxShareUtil.bitmap2Bytes(bitmap, 128)
        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession // 目前只支持会话
        api.sendReq(req)
    }
}