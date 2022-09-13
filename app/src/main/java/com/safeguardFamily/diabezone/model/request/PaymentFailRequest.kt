package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class PaymentFailRequest(
    var amount: String?,
    var error: Error?,
    var pid: String?,
    var puid: String?,
    @SerializedName("sel_date")
    var selDate: String?,
    var slot: String?,
    var type: String?,
    var uid: String?
)

data class Error(
    var code: String?,
    var description: String?,
    var reason: String?,
    var source: String?,
    var step: String?
)