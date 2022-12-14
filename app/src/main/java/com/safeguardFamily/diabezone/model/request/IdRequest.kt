package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class IdRequest(
    @SerializedName("uid") val uid: String
)