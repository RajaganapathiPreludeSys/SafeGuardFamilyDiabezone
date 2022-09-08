package com.safeguardFamily.diabezone.model.request

data class PaymentFailRequest(
    var error: Error?
)

data class Error(
    var code: String?,
    var description: String?,
    var reason: String?,
    var source: String?,
    var step: String?
)