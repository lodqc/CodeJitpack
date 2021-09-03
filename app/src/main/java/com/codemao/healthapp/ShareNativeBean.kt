package com.codemao.healthapp


import com.google.gson.annotations.SerializedName

data class ShareNativeBean(
    @SerializedName("payload")
    val payload: Payload?
)

data class Payload(
    @SerializedName("desc")
    val desc: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("image_base64")
    val imageBase64: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: Int?,
    @SerializedName("url")
    val url: String?
)