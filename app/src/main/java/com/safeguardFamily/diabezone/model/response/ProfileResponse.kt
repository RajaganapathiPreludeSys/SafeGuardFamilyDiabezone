package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    var health_coach: Provider?,
    var is_member: Boolean?,
    var membership: List<Membership>?,
    var past_appointments: List<PastAppointment>?,
    var user: User?,
    @SerializedName("contact_info")
    var contactInfo: ContactInfo?
)

data class Membership(
    val mid: Int,
    val pack_id: Int,
    val pack_name: String,
    val uid: Long,
    val validity: String
)

data class PastAppointment(
    val aid: String?,
    var booking_date: String?,
    var booking_status: Int?,
    var provider: Provider?,
    var puid: String?,
    var slot: String?,
    var uid: String?
)

data class ContactInfo(
    var cc: String?,
    var email: String?,
    var mobile: String?,
    var pic: String?,
    @SerializedName("whatsapp_no")
    var whatsappNo: String?
)
