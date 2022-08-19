package com.safeguardFamily.diabezone.model.response

data class SlotsResponse(
    val is_member: Boolean,
    val slots: List<AvailableSlot>
)
