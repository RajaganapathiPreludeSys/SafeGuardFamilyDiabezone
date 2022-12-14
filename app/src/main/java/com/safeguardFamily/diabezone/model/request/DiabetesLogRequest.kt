package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class DiabetesLogRequest(
    @SerializedName("lid") var lid: String? = null,
    @SerializedName("log_value") var logValue: Int,
    @SerializedName("measure_date") var measureDate: String,
    @SerializedName("period") var period: String,
    @SerializedName("uid") var uid: String
)
