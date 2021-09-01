package com.codemao.share

import android.content.Context
import android.graphics.Bitmap

interface ShareCallBack {
    fun loadBitemap(minProgramCover: String): Bitmap
    fun getPermission(context: Context?, permissions: Array<String>, function: (granted:Boolean) -> Unit)
}