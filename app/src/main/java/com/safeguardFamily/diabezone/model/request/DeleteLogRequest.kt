package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class DeleteLogRequest(
    @SerializedName("uid") val uid: String,
    @SerializedName("lid") val lid: String
)