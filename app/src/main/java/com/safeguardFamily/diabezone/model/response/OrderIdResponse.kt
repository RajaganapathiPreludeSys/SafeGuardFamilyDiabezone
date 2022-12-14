package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class OrderIdResponse(
    @SerializedName("order_ID") var order_ID: String,
    @SerializedName("receipt") var receipt: String
)
