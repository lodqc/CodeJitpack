package com.codemao.sensors

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.lxj.xpopup.impl.FullScreenPopupView
import kotlinx.android.synthetic.main.sensors_dialog_request_permission.view.*
import java.lang.Exception

class RequestPermissionPop(
    context: Context,
    val permission: List<PermissionBean>? = null,
    val checkable: Boolean = true
) : FullScreenPopupView(context) {

    companion object {
        @JvmStatic
        fun addSensorsPermissionBean(): PermissionBean {
            return PermissionBean(
                R.drawable.sensors_ic_permission_device_info,
                R.string.sensors_permission_phone_state,
                R.string.sensors_permission_tip
            )
        }
    }

    private var mCallback: ((Boolean, List<PermissionBean>?) -> Unit)? = null
    private var checkedCount = 0

    override fun getImplLayoutId() = R.layout.sensors_dialog_request_permission

    override fun initPopupContent() {
        super.initPopupContent()
        val negative = dialog.findViewById<View>(R.id.b_disagree)
        negative.addClickScale(0.95f, 150L)
        negative.setOnClickListener { v: View? ->
            mCallback?.invoke(false, permission)
            dialog.dismiss()
        }
        b_agree.addClickScale(0.95f, 150L)
        b_agree.setOnClickListener { v: View? ->
            dialog.dismiss()
            mCallback?.invoke(true, permission)
        }
        //单独处理
        permission?.run {
            forEach { data ->
                LayoutInflater.from(context)
                    .inflate(R.layout.sensors_item_permission_request, ll_permission_list, false)
                    ?.let {
                        bindView(it, data, checkable)
                        ll_permission_list.addView(
                            it,
                            kotlin.math.min(ll_permission_list.childCount, 1)
                        )
                    }
            }
        }

    }

    fun listenClick(callback: ((Boolean, List<PermissionBean>?) -> Unit)? = null): RequestPermissionPop {
        mCallback = callback
        return this
    }

    ///////////////////////////////////////////////////////////////////////////
    // 处理单个权限分开（非权限组），添加对应权限文案
    //tip：方法不与上面业务通用
    ///////////////////////////////////////////////////////////////////////////
    fun setTitle(@StringRes titleRes: Int, textSize: Float = 18f): RequestPermissionPop {
        tv_permission_title.setText(titleRes)
        tv_permission_title.textSize = textSize
        return this
    }

    data class PermissionBean(
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        @StringRes val content: Int
    )

    private fun bindView(view: View?, data: PermissionBean, checkable: Boolean) {
        view?.findViewById<ImageView>(R.id.permission_icon)?.setImageResource(data.icon)
        view?.findViewById<TextView>(R.id.permission_title)?.setText(data.title)
        view?.findViewById<TextView>(R.id.permission_content)?.setText(data.content)
        if (checkable) {
            checkedCount++
            view?.findViewById<ImageView>(R.id.permission_check)?.click {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    checkedCount++
                    (it as ImageView).setImageResource(R.drawable.sensors_ic_check)
                } else {
                    checkedCount--
                    (it as ImageView).setImageResource(R.drawable.sensors_ic_uncheck)
                }
                b_agree.background =
                    drawable(if (checkedCount == 0) R.drawable.sensors_bg_c9c9dc_corner_8 else R.drawable.sensors_bg_7245ff_corner_8)
            }?.isSelected = true
        }
    }

    fun initGoSettingBtn(): RequestPermissionPop {
        b_agree.isVisible = false
        confirm_container.isVisible = true
        confirm_cancel.addClickScale().click {
            mCallback?.invoke(false, permission)
            dismiss()
        }
        go_settings.addClickScale().click {
            mCallback?.invoke(false, permission)
            dismiss()
            simpleSetting(context)
        }
        return this
    }

    ///////////////////////////////////////////////////////////////////////////
    // 方法不与上面业务通用
    ///////////////////////////////////////////////////////////////////////////

}

fun simpleSetting(context: Context) {
    var intent = Intent()
    try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //Log.e("HLQ_Struggle", "******************当前手机型号为：" + getMobileType());
        // 将用户引导到系统设置页面
        if (Build.VERSION.SDK_INT >= 9) {
            //  Log.e("HLQ_Struggle", "APPLICATION_DETAILS_SETTINGS");
            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            intent.data = Uri.fromParts("package", context.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.action = Intent.ACTION_VIEW
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            intent.putExtra("com.android.settings.ApplicationPkgName", context.packageName)
        }
        context.startActivity(intent)
    } catch (e: Exception) { //抛出异常就直接打开设置页面
        // Log.e("HLQ_Struggle", e.getLocalizedMessage());
        intent = Intent(Settings.ACTION_SETTINGS)
        context.startActivity(intent)
    }
}