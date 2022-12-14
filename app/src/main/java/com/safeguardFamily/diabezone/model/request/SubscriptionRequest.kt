package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class SubscriptionRequest(
    @SerializedName("payment_response") var paymentResponse: PaymentResponse?,
    @SerializedName("pid") var pid: String?,
    @SerializedName("uid") var uid: String?
)

data class PaymentResponse(
    @SerializedName("key") var key: String?
)