package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class InitRequest(
    @SerializedName("platform")
    var platform: String?,
    @SerializedName("token")
    var token: String?,
    @SerializedName("uid")
    var uid: String?,
    @SerializedName("version")
    var version: String?
)