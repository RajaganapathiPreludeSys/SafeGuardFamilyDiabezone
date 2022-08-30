package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class DiabetesLogRequest(
    var lid: String? = null,
    @SerializedName("log_value")
    var logValue: Int,
    @SerializedName("measure_date")
    var measureDate: String,
    var period: String,
    var uid: String
)
