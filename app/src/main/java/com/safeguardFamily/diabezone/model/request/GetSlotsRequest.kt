package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class GetSlotsRequest(
    @SerializedName("uid") val uid: String,
    @SerializedName("puid") val puid: String,
)
