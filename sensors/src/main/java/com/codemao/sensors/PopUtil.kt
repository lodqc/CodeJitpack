package com.codemao.sensors

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.AppOpsManagerCompat
import androidx.core.content.ContextCompat
import com.codemao.sensors.RequestPermissionPop.Companion.addSensorsPermissionBean
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import pub.devrel.easypermissions.EasyPermissions

object PopUtil {
    const val All_PERMISSION_REQUEST_CODE = 101
    fun checkSensorsPermissionDialog(
        ctx: Activity,
        block: ((Boolean) -> Unit)? = null
    ): Boolean {
        val needPermission = arrayOf(Manifest.permission.READ_PHONE_STATE)
        val permissionLabel = arrayOf(addSensorsPermissionBean())
        val permissions = ArrayList<RequestPermissionPop.PermissionBean>()
        needPermission.forEachIndexed { index, p ->
            if (!hasPermission(ctx, p)) {
                permissions.add(permissionLabel[index])

            }
        }
        if (permissions.isNotEmpty()) {
            val pop =
                RequestPermissionPop(context = ctx, permission = permissions, checkable = false)
            XPopup.Builder(ctx)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .autoDismiss(false)
                .popupAnimation(PopupAnimation.NoAnimation)
                .asCustom(pop.listenClick { pass: Boolean, permissionList: List<RequestPermissionPop.PermissionBean?>? ->
                    if (pass && permissionList?.isNotEmpty() == true) {
                        EasyPermissions.requestPermissions(
                            ctx, "需要权限，请同意授权",
                            All_PERMISSION_REQUEST_CODE,
                            *needPermission
                        )
                    }
                    block?.invoke(pass)
                }).show()
//            pop.post {
//                pop.setTitle(R.string.sensors_request_permission_title_again, 18f)
//                if (hasReject) {
//                    pop.initGoSettingBtn()
//                }
//            }
            SensorsSPUtil.getInstance().put(Manifest.permission.READ_PHONE_STATE, true)
        }
        return permissions.isEmpty()
    }

    /**
     * 兼容部分厂商更改系统导致的权限检测不准 eg 360
     * @param context
     * @param permissions
     * @return true hasPermission
     */
    fun hasPermission(context: Context, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        for (permission in permissions) {
            if ("Xiaomi" != Build.BRAND && "Redmi" != Build.BRAND) { // 小米Android10调用该方法会直接弹出权限
                val op = AppOpsManagerCompat.permissionToOp(permission!!)
                if (TextUtils.isEmpty(op)) continue
                val result = AppOpsManagerCompat.noteProxyOp(
                    context,
                    op!!, context.packageName
                )
                if (result == AppOpsManagerCompat.MODE_IGNORED) return false
            }
            val result = ContextCompat.checkSelfPermission(
                context,
                permission!!
            )
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }
}