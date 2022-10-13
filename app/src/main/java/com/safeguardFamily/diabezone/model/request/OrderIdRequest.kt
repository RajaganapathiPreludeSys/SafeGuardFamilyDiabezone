package com.safeguardFamily.diabezone.model.request

data class OrderIdRequest(
    var amount: String,
    var pid: String,
    var type: String,
    var uid: String
)