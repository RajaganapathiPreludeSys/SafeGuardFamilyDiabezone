package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class DiabetesResponse(
    var graph: Graph?,
    var logs: List<Log>?,
    @SerializedName("pdf_url")
    var pdfUrl: String?
)

data class Log(
    var lid: String?,
    @SerializedName("log_value")
    var logValue: String?,
    @SerializedName("measure_date")
    var measureDate: String?,
    @SerializedName("measure_unit")
    var measureUnit: String?,
    var message: String?,
    var period: String?,
    var status: String?,
    var symbol: String?,
    var uid: String?
)
