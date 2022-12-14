package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class DiabetesResponse(
    @SerializedName("graph") var graph: Graph?,
    @SerializedName("logs") var logs: List<Log>?,
    @SerializedName("pdf_url") var pdfUrl: String?
)

data class Log(
    @SerializedName("lid") var lid: String?,
    @SerializedName("log_value") var logValue: String?,
    @SerializedName("measure_date") var measureDate: String?,
    @SerializedName("measure_unit") var measureUnit: String?,
    @SerializedName("message") var message: String?,
    @SerializedName("period") var period: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("symbol") var symbol: String?,
    @SerializedName("uid") var uid: String?
)
