package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("otps") var otps: List<String>? = null
)