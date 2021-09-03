package com.codemao.share

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.codemao.share.NewWxShareUtil.api
import com.codemao.share.ShareManager.WX_USERNAME
import com.codemao.share.ShareManager.base64ToBitmap
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.BottomPopupView
import com.tencent.connect.share.QQShare
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import io.reactivex.android.schedulers.AndroidSchedulers
import java.net.URLEncoder


/**
 * <pre>
 *     author : yangliu
 *     e-mail : yangliu@codemao.cn
 *     time   : 2020/12/28
 *     desc   :
 * </pre>
 */
interface ShareListener {
    fun showMiaoCode()
}

//分享文本
fun showShareTextPop(
    context: Context,
    text: String?,
    title: String,
    showMiaoCode: ShareListener? = null,
) {
    XPopup.Builder(context)
        .enableDrag(true)
        .asCustom(
            GeneralSharePop(context)
                .shareText(text)
                .shareTitle(title)
                .showMiaoCode(showMiaoCode)
                .setType(2)
        )
        .show()
}

fun showNativePop(
    context: Context,
    type: Int?,
    title: String?,
    url: String?,
    image_url: String?,
    image_base64: String?,
    desc: String?,
    showMiaoCode: ShareListener? = null,
) {
    XPopup.Builder(context)
        .enableDrag(true)
        .asCustom(
            GeneralSharePop(context)
                .shareText(desc)
                .shareTitle(title)
                .setUrl(url)
                .imageBase64(image_base64)
                .imageUrl(image_url)
                .showMiaoCode(showMiaoCode)
                .setType(type)
        )
        .show()
}

//分享图片地址
fun showShareImgPop(
    context: Context,
    imgPath: String?,
    showMiaoCode: ShareListener? = null,
    shareText: String? = null,
) {
    XPopup.Builder(context)
        .enableDrag(true)
        .asCustom(
            GeneralSharePop(context)
                .shareText(shareText)//这个需要在sharebitmap前面
                .shareImage(imgPath)
                .showMiaoCode(showMiaoCode)
                .setType(1)
        )
        .show()
}

//分享base64的图片åå
fun showShareBase64Pop(
    context: Context,
    base64: String?,
    showMiaoCode: ShareListener? = null,
    shareText: String? = null,
) {
    XPopup.Builder(context)
        .enableDrag(true)
        .asCustom(
            GeneralSharePop(context)
                .shareText(shareText)//这个需要在sharebitmap前面
                .shareBitmap(base64)
                .showMiaoCode(showMiaoCode)
                .setType(1)
        )
        .show()
}

//分享base64的图片åå
fun showShareBitmapPop(
    context: Context,
    bitmap: Bitmap?,
    showMiaoCode: ShareListener? = null,
    shareText: String? = null,
) {
    XPopup.Builder(context)
        .enableDrag(true)
        .asCustom(
            GeneralSharePop(context)
                .shareText(shareText)
                .shareBitmap(bitmap)
                .showMiaoCode(showMiaoCode)
                .setType(1)
        )
        .show()
}

class GeneralSharePop(context: Context) : BottomPopupView(context), View.OnClickListener {

    override fun getImplLayoutId() = R.layout.share_layout_bottom_share

    private var group_wx: Group? = null
    private var group_memory: Group? = null
    private var group_qq: Group? = null
    private var group_miao: Group? = null
    private var group_copy_link: Group? = null

    private var iv_wx: View? = null
    private var iv_memory: View? = null
    private var iv_qq: View? = null
    private var bg_miao_code: View? = null
    private var bg_miao_url: View? = null
    private var close: View? = null
    private var tv_create_url: TextView? = null

    private var shareText: String? = ""
    private var shareTitle: String? = ""
    private var shareImgPath: String? = ""
    private var shareBase64: String? = ""
    private var shareBitmap: Bitmap? = null
    private var hasShare: Boolean = false//是否有分享动作  埋点使用
    private var isShareText = true//是否是分享文本
    var permissions = arrayOf(
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE"
    )
    var miniProgramCoverUrl = ""

    private val shareUtil by lazy {
        ShareUtil(context)
    }

    private val shareQQ by lazy {
        Tencent.createInstance(ShareManager.QQ_APP_ID, getContext());
    }


    fun shareText(text: String?): GeneralSharePop {
        shareText = text
        isShareText = true
        return this
    }

    fun shareTitle(text: String?): GeneralSharePop {
        shareTitle = text
        return this
    }

    fun shareImage(path: String?): GeneralSharePop {
        shareImgPath = path
        isShareText = false
        return this
    }

    fun shareBitmap(base64: String?): GeneralSharePop {
        shareBase64 = base64
        isShareText = false
        return this
    }

    fun shareBitmap(base64: Bitmap?): GeneralSharePop {
        shareBitmap = base64
        isShareText = false
        return this
    }

    fun showMiaoCode(show: ShareListener?): GeneralSharePop {
        mShowMiaoCodeListener = show
        return this
    }


    private val shareQQlistener by lazy {
        object : IUiListener {
            override fun onComplete(p0: Any?) {
                println(p0)
            }

            override fun onError(p0: UiError?) {
                println(p0?.errorMessage)

            }

            override fun onCancel() {

            }

        }
    }

    private var mShowMiaoCodeListener: ShareListener? = null
    //private var showMiaoCode = false //是否显示喵口令


    override fun initPopupContent() {
        super.initPopupContent()

        close = findViewById(R.id.iv_close)
        group_wx = findViewById(R.id.group_wx)
        group_memory = findViewById(R.id.group_memory)
        group_qq = findViewById(R.id.group_qq)
        group_miao = findViewById(R.id.group_miao)
        group_copy_link = findViewById(R.id.group_copy_link)

        tv_create_url = findViewById(R.id.tv_create_url)

        iv_wx = findViewById(R.id.iv_wx)
        iv_memory = findViewById(R.id.iv_memory)
        iv_qq = findViewById(R.id.iv_qq)
        bg_miao_code = findViewById(R.id.bg_miao_code)
        bg_miao_url = findViewById(R.id.bg_miao_url)

        group_miao?.apply {
            isVisible = mShowMiaoCodeListener != null
            bg_miao_code?.setOnClickListener(this@GeneralSharePop)
        }

        group_wx?.apply {
            isVisible = shareUtil.isWxInstalled
        }

        iv_wx?.setOnClickListener(this)


        group_memory?.apply {
            isVisible = shareUtil.isWxInstalled
            iv_memory?.setOnClickListener(this@GeneralSharePop)
        }
        group_qq?.apply {
//            isVisible = shareQQ.isQQInstalled(context)
            iv_qq?.setOnClickListener(this@GeneralSharePop)
        }

        group_copy_link?.apply {
            bg_miao_url?.setOnClickListener(this@GeneralSharePop)
        }
        close?.setOnClickListener(this)

        shareText?.apply {
            if (this.startsWith("http")) {
                tv_create_url?.text = "复制链接"
            }
        }
    }

    private fun shareQQImage() {
        if (!TextUtils.isEmpty(shareImgPath)) {
            shareQQbyPath(shareImgPath)
        } else if (!TextUtils.isEmpty(shareBase64)) {
            base64ToBitmap(shareBase64)?.also {
                WxShareUtil.bitmapSaveSDCardFile(context, it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ file ->
                        shareQQbyPath(file?.absolutePath)
                    }, {
                        toastError("分享失败")
                    })
            } ?: kotlin.run {
                toastError("分享失败")
            }
        } else if (shareBitmap != null) {
            WxShareUtil.bitmapSaveSDCardFile(context, shareBitmap!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    shareQQbyPath(file?.absolutePath)
                }, {
                    toastError("分享失败")
                })
        }
    }

    private fun shareQQbyPath(path: String?) {
        val params = Bundle()
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path)
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
        getActivityFromContext(context)?.let {
            shareQQ.shareToQQ(it, params, shareQQlistener)
        } ?: kotlin.run {
            toastError("分享失败")
        }
    }

    fun toastError(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun toastSuccess(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }


    private fun shareQQText() {
        val intent = Intent("android.intent.action.SEND")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
        url?.let {

        }
        if(type == 3){
            intent.putExtra(Intent.EXTRA_TEXT, url)
        }else{
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.component =
            ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity")
        context.startActivity(intent)
    }

    private fun getActivityFromContext(context: Context?): Activity? {
        try {
            if (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                return context.baseContext as Activity
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun copy2link() {
        val clip = ClipData.newPlainText("simple text", shareText ?: "")
        val clipManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        toastSuccess("已复制成功，快去分享吧")
        clipManager.setPrimaryClip(clip)
    }


    override fun doAfterDismiss() {
        super.doAfterDismiss()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v) {
                close -> {
                    dismiss()
                }
                iv_wx -> {
                    hasShare = true
                    ShareManager.listeneWxResp(context)
                    if (type != 1) {
                        if (TextUtils.isEmpty(url)) {
                            shareText?.let { it1 ->
                                NewWxShareUtil.shareText(
                                    it1,
                                    SendMessageToWX.Req.WXSceneSession
                                )
                            }
                        }else{
                            shareTitle?.let { it1 ->
                                shareText?.let { it2 ->
                                    url?.let { it3 ->
                                        NewWxShareUtil.shareUrl(
                                            it3,
                                            it1,
                                            it2,
                                            SendMessageToWX.Req.WXSceneSession,
                                            imageUrl,
                                            imageBase64
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(shareImgPath)) {
                            WxShareUtil.shareImgToWXFriend(
                                context,
                                shareImgPath,
                                null
                            )
                        } else if (!TextUtils.isEmpty(shareBase64)) {
                            shareUtil.shareImage2Person(context, shareBase64!!, null)
                        } else if (shareBitmap != null) {
                            WxShareUtil.shareBitmapToWXFriend(
                                context,
                                shareBitmap,
                                null
                            )
                        }
                    }
                    dismiss()
                }

                iv_memory -> {
                    hasShare = true
                    ShareManager.listeneWxResp(context)
                    if (type != 1) {
                        shareUtil.shareText2Memory(if(type==3) url!! else shareText!!, shareTitle ?: "探月少儿编程")
                    } else {
                        if (!TextUtils.isEmpty(shareImgPath)) {
                            WxShareUtil.shareImgToWXMoment(
                                context,
                                shareImgPath,
                                null
                            )
                        } else if (!TextUtils.isEmpty(shareBase64)) {
                            shareUtil.shareImage2Moment(context, shareBase64!!, null)
                        } else if (shareBitmap != null) {
                            WxShareUtil.shareBitmapToWXMoment(
                                context,
                                shareBitmap,
                                null
                            )
                        }
                    }
                    dismiss()
                }

                iv_qq -> {
                    hasShare = true
                    if (type != 1) {
                        shareQQText()
                    } else {
                        ShareManager.getPermission(context, permissions) {
                            if (it) {
                                shareQQImage()
                                dismiss()
                            } else {
                                toastError("需要读写权限，请授权")
                                dismiss()
                            }
                        }
                    }

                }

                bg_miao_code -> {
                    hasShare = true
                    mShowMiaoCodeListener?.showMiaoCode()
                    dismiss()
                }

                bg_miao_url -> {
                    hasShare = true
                    copy2link()
                    dismiss()
                }
                else -> {

                }
            }
        }
    }

    // 分享类型  1.图片 2.文本 3.链接
    var type: Int? = 0
    var url: String? = ""
    var imageBase64: String? = ""
    var imageUrl: String? = ""
    fun setType(type: Int?): GeneralSharePop {
        this.type = type
        return this
    }

    fun setUrl(url: String?): GeneralSharePop {
        this.url = url
        return this
    }

    fun imageBase64(imageBase64: String?): GeneralSharePop {
        this.imageBase64 = imageBase64
        return this
    }

    fun imageUrl(imageUrl: String?): GeneralSharePop {
        this.imageUrl = imageUrl
        return this
    }
}