package com.safeguardFamily.diabezone.model.request

data class MobileNumberRequest(
    var cc: String = "91",
    var mobile: String = "",
    var otp: String? = ""
)