package com.safeguardFamily.diabezone.model.request

import com.google.gson.annotations.SerializedName

data class CreateAppointmentRequest (
    @SerializedName("uid")
    val uid: String,
    @SerializedName("puid")
    val puid: String,
    @SerializedName("sel_date")
    val sel_date: String,
    @SerializedName("aid")
    val aid: String? = null,
    @SerializedName("razorPayKey")
    val razorPayKey: String? = null,
    @SerializedName("slot")
    val slot: String
)
