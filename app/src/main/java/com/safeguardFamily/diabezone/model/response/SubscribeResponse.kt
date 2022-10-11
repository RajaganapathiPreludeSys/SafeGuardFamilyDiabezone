package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class SubscribeResponse(
    var subscription: Subscription?
)

data class Subscription(
    @SerializedName("end_date")
    var endDate: String?,
    var pid: String?,
    var sid: Int?,
    @SerializedName("start_date")
    var startDate: String?,
    @SerializedName("subscription_status")
    var subscriptionStatus: Int?,
    var uid: String?
)