package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class SubscribeResponse(
    @SerializedName("slots") var subscription: Subscription?
)

data class Subscription(
    @SerializedName("end_date") var endDate: String?,
    @SerializedName("pid") var pid: String?,
    @SerializedName("sid") var sid: Int?,
    @SerializedName("start_date") var startDate: String?,
    @SerializedName("subscription_status") var subscriptionStatus: Int?,
    @SerializedName("uid") var uid: String?
)