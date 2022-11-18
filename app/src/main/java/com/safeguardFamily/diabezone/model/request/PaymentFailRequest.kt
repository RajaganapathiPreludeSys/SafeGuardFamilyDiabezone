package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class PaymentFailRequest(
    @SerializedName("amount") var amount: String?,
    @SerializedName("error") var error: Error?,
    @SerializedName("pid") var pid: String?,
    @SerializedName("puid") var puid: String?,
    @SerializedName("sel_date") var selDate: String?,
    @SerializedName("slot") var slot: String?,
    @SerializedName("type") var type: String?,
    @SerializedName("uid") var uid: String?
)

data class Error(
    @SerializedName("code") var code: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("reason") var reason: String?,
    @SerializedName("source") var source: String?,
    @SerializedName("step") var step: String?
)