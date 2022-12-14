package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class SlotsResponse(
    @SerializedName("is_member") var is_member: Boolean,
    @SerializedName("slots") var slots: List<AvailableSlot>
)
