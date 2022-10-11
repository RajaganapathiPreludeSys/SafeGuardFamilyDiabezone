package com.safeguardFamily.diabezone.model.request

data class CreateAppointmentRequest (
    val uid: String,
    val puid: String,
    val sel_date: String,
    val aid: String? = null,
    val razorPayKey: String? = null,
    val slot: String
)
