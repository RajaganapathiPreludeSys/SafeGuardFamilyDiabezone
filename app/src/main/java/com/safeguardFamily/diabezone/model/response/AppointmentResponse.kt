package com.safeguardFamily.diabezone.model.response
import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    var appointment: Appointment,
    @SerializedName("order_id")
    var orderId: String?,
    var receipt: String?
)
