package com.codemao.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX

import com.tencent.mm.opensdk.modelmsg.WXWebpageObject





object NewWxShareUtil {
    val api = WXAPIFactory.createWXAPI(ShareManager.getContext(), ShareManager.WX_APP_ID)
    fun shareText(text:String,mTargetScene:Int){
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

    fun shareUrl(webpageUrl:String,title:String,description:String,mTargetScene:Int,imgUrl: String?,imgBase64:String?){
        if(TextUtils.isEmpty(imgUrl)&&TextUtils.isEmpty(imgBase64)){
            val decodeResource = BitmapFactory.decodeResource(
                ShareManager.getContext().resources,
                ShareManager.shareDrawableId
            )
            shareUrl(webpageUrl,title,description,mTargetScene,decodeResource)
        }
        if(!TextUtils.isEmpty(imgUrl)){
            imgUrl?.let {
                ShareManager.loadBitemap(it){
                    shareUrl(webpageUrl,title,description,mTargetScene,it)
                }
            }
        }
        if(!TextUtils.isEmpty(imgBase64)){
            ShareManager.base64ToBitmap(imgBase64)?.run {
                shareUrl(webpageUrl,title,description,mTargetScene,this)
            }
        }
    }

    fun shareUrl(webpageUrl:String,title:String,description:String,mTargetScene:Int,bitmap: Bitmap){
        //初始化一个WXWebpageObject，填写url
        //初始化一个WXWebpageObject，填写url
        val webpage = WXWebpageObject()
        webpage.webpageUrl = webpageUrl

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        val msg = WXMediaMessage(webpage)
        msg.title = title
        msg.description =description
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
}