package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class MobileNumberRequest(
    @SerializedName("cc") var cc: String = "91",
    @SerializedName("mobile") var mobile: String = "",
    @SerializedName("otp") var otp: String? = ""
)