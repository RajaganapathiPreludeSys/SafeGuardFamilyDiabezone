package com.safeguardFamily.diabezone.model.response

data class ProfileResponse(
    var health_coach: Provider?,
    var is_member: Boolean?,
    var membership: List<Membership>?,
    var past_appointments: List<PastAppointment>?,
    var user: User?
)

data class Membership(
    val mid: Int,
    val pack_id: Int,
    val pack_name: String,
    val uid: Long,
    val validity: String
)

data class PastAppointment(
    var aid: Int?,
    var booking_date: String?,
    var booking_status: Int?,
    var provider: Provider?,
    var puid: String?,
    var slot: String?,
    var uid: String?
)
