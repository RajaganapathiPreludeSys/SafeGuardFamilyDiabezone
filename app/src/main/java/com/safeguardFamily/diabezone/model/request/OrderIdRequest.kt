package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class OrderIdRequest(
    @SerializedName("amount") var amount: String,
    @SerializedName("pid") var pid: String,
    @SerializedName("type") var type: String,
    @SerializedName("uid") var uid: String
)