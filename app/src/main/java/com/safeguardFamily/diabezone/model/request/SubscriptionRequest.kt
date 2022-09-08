package com.safeguardFamily.diabezone.model.request
import com.google.gson.annotations.SerializedName

data class SubscriptionRequest(
    @SerializedName("payment_response")
    var paymentResponse: PaymentResponse?,
    var pid: String?,
    var uid: String?
)

data class PaymentResponse(
    var key: String?
)