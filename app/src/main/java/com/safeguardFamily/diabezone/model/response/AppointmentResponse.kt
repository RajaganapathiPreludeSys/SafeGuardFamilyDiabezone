package com.safeguardFamily.diabezone.model.response

data class AppointmentResponse(
    val appointment: Appointment
)

data class Appointment(
    val aid: Int,
    val booking_status: Int,
    val date: String,
    val provider: Provider,
    val puid: String,
    val slot: String,
    val uid: String
)