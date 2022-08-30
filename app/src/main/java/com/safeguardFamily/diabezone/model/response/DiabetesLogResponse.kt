package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName
import com.safeguardFamily.diabezone.model.request.DiabetesLogRequest

data class DiabetesLogResponse(
    @SerializedName("diabetes-log")
    var diabetesLog: DiabetesLogRequest?
)
