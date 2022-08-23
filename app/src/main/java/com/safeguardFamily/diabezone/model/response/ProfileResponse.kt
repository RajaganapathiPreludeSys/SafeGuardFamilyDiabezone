package com.safeguardFamily.diabezone.model.response

data class ProfileResponse(
    val health_coach: Provider,
    val is_member: Boolean,
    val membership: List<Membership>,
    val past_appointments: List<PastAppointment>,
    val user: User
)

data class Membership(
    val mid: Int,
    val pack_id: Int,
    val pack_name: String,
    val uid: Long,
    val validity: String
)

data class PastAppointment(
    val appointments: List<String>,
    val title: String
)

data class User(
    val cc: String,
    val email: String,
    val mobile: String,
    val name: String,
    val pic: String,
    val uid: String
)