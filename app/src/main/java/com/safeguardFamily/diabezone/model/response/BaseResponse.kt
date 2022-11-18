package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("error") val error: String?,
    @SerializedName("data") val data: T
)