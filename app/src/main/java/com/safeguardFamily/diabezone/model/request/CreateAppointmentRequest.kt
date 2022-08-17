package com.safeguardFamily.diabezone.model.request

data class CreateAppointmentRequest(
    val uid: String = "164786007638897937",
    val puid: String,
    val sel_date: String,
    val slot: String
)
