package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("appointment") var appointment: Appointment,
    @SerializedName("order_id") var orderId: String?,
    @SerializedName("receipt") var receipt: String?
)
