package com.codemao.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import cn.codemao.android.account.util.WechatManager
import com.codemao.share.ShareManager.Bitmap2Bytes
import com.codemao.share.ShareManager.base64ToBitmap
import com.tencent.mm.opensdk.openapi.IWXAPI
import cn.codemao.android.share.interfaces.IshareResult
import com.codemao.share.NewWxShareUtil.api
import com.codemao.share.ShareManager.shareDrawableId
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * <pre>
 * author : yangliu
 * e-mail : yangliu@codemao.cn
 * time   : 2020/06/01
 * desc   : 分享工具类
</pre> *
 */
class ShareUtil(context: Context?) {
    var isWxInstalled: Boolean
    fun openWxApp(): Boolean {
        return api.openWXApp()
    }

    /**
     * 分享到微信好友
     *
     * @param base64
     * @param ishareResult
     */
    fun shareImage2Person(context: Context, base64: String, ishareResult: IshareResult?) {
        share(context, base64, ishareResult, false)
    }

    /**
     * 分享微信朋友圈
     *
     * @param base64
     * @param ishareResult
     */
    fun shareImage2Moment(context: Context, base64: String, ishareResult: IshareResult?) {
        share(context, base64, ishareResult, true)
    }

    private fun share(
        context: Context,
        base64: String,
        ishareResult: IshareResult?,
        isMoment: Boolean
    ) {
        if (TextUtils.isEmpty(base64)) {
            ishareResult?.onFailure(BASE64_ERROR)
            return
        }
        val bitmap: Bitmap = base64ToBitmap(base64)!!
        if (bitmap == null) {
            ishareResult?.onFailure(CREATE_BITMAP_FAILED)
        } else {
            if (isWxInstalled) {
                if (isMoment) {
                    WxShareUtil.shareBitmapToWXMoment(context, bitmap,ishareResult)
                    //WXMomentShareImage(bitmap, ishareResult);
                } else {
                    WxShareUtil.shareBitmapToWXFriend(context, bitmap,ishareResult)
                    // WXShareImage(bitmap, ishareResult);
                }
            } else {
                ishareResult?.onFailure(UNINSTALL_WX)
            }
        }
    }

    /**
     * 分享微信朋友圈
     *
     * @param finalBitmap
     * @param ishareResult
     */
    private fun WXMomentShareImage(finalBitmap: Bitmap, ishareResult: IshareResult) {

        //这里添加微信安装判断
        WechatManager.getInstance().ishareResult = ishareResult
        //创建一个WXImageObject对象，用于封装要发送的图片
        val imageObject = WXImageObject(finalBitmap)
        //创建一个WXMediaMessage对象

        /* //兼容android11版本
        if (api.getWXAppSupportAPI() >= 0x27000D00 && android.os.Build.VERSION.SDK_INT >= 30) {
            imageObject.setImagePath(getFileUri(context, localImageUrl));
        } else {
            imageObject.setImagePath(localImageUrl);
        }*/
        val msg = WXMediaMessage()
        msg.mediaObject = imageObject
        val req = SendMessageToWX.Req()
        //transaction字段用于唯一标识一个请求，这个必须有，否则会出错
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg

        //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
        req.scene = SendMessageToWX.Req.WXSceneTimeline
        api.sendReq(req)
    }

    /**
     * 分享微信好友
     *
     * @param finalBitmap
     * @param ishareResult
     */
    private fun WXShareImage(finalBitmap: Bitmap, ishareResult: IshareResult) {
        WechatManager.getInstance().ishareResult = ishareResult
        //创建一个WXImageObject对象，用于封装要发送的图片
        val imageObject = WXImageObject(finalBitmap)

        //        imageObject.setImagePath(fileName);
        //创建一个WXMediaMessage对象
        val msg = WXMediaMessage()
        msg.mediaObject = imageObject
        val req = SendMessageToWX.Req()
        //transaction字段用于唯一标识一个请求，这个必须有，否则会出错
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg

        //表示发送给朋友圈  WXSceneTimeline  表示发送给朋友  WXSceneSession
        req.scene = SendMessageToWX.Req.WXSceneSession
        api.sendReq(req)
    }

    /**
     * 分享文字给好友
     */
    fun shareText(text: String, des: String?) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        var text = text
        if (TextUtils.isEmpty(text)) text = " "
        val msg = WXMediaMessage()
        if (text.startsWith("http")) {
            val wxWebpageObject = WXWebpageObject()
            wxWebpageObject.webpageUrl = text
            //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
            msg.mediaObject = wxWebpageObject
            msg.title = des
            val thumbBmp = BitmapFactory.decodeResource(
                ShareManager.getContext().resources,
                shareDrawableId
            )
            msg.thumbData = Bitmap2Bytes(thumbBmp)
        } else {
            val textObj = WXTextObject()
            textObj.text = text
            //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
            msg.mediaObject = textObj
            msg.description = des
        }
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession
        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 分享文字给好友
     */
    fun shareText2Memory(text: String, des: String?) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        var text = text
        if (TextUtils.isEmpty(text)) text = " "
        val msg = WXMediaMessage()
        if (text.startsWith("http")) {
            val wxWebpageObject = WXWebpageObject()
            wxWebpageObject.webpageUrl = text
            //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
            msg.mediaObject = wxWebpageObject
            msg.title = des
            val thumbBmp = BitmapFactory.decodeResource(
                ShareManager.getContext().resources,
                shareDrawableId
            ) //微信分享图标不能超过30K  请注意
            msg.thumbData = Bitmap2Bytes(thumbBmp)
        } else {
            val textObj = WXTextObject()
            textObj.text = text
            //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
            msg.mediaObject = textObj
            msg.description = des
        }
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneTimeline
        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    /**
     * 分享图片
     *
     * @param localFilePath 本地图片路径
     * @param type          1 对话 2 朋友圈
     */
    fun sharePic2Scene(localFilePath: String?, type: Int) {
        //因为是已经压缩过的 就不再压缩了
        val bitmap = BitmapFactory.decodeFile(localFilePath) ?: return
        //初始化 WXImageObject 和 WXMediaMessage 对象
        val imgObj = WXImageObject(bitmap)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        bitmap.recycle()
        //构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        if (type == 1) {
            req.scene = SendMessageToWX.Req.WXSceneSession
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline
        }
        //调用api接口，发送数据到微信
        api.sendReq(req)
    }

    fun sharePic2Scene(context: Context?, bitmap: Bitmap?, type: Int) {
        if (bitmap == null) {
            return
        }
        /*   //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        bitmap.recycle();
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;*/if (type == 1) {
            WxShareUtil.shareBitmapToWXFriend(context, bitmap, null)
            //req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            // req.scene = SendMessageToWX.Req.WXSceneTimeline;
            WxShareUtil.shareBitmapToWXMoment(context, bitmap, null)
        }
        //调用api接口，发送数据到微信
        // api.sendReq(req);
    }

    companion object {
        const val CREATE_BITMAP_FAILED = "CREATE_BITMAP_FAILED" //生成bitmap异常
        const val UNINSTALL_WX = "UNINSTALL_WX" //未安装微信
        const val BASE64_ERROR = "BASE64_ERROR" //传入的参数异常
    }

    init {
        isWxInstalled = api.isWXAppInstalled
    }
}