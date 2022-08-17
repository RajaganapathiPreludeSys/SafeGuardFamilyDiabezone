package com.safeguardFamily.diabezone.model.response

data class ProvidersResponse(
    val is_member: Int,
    val providers: List<Provider>
)

data class Provider(
    val about: String,
    val available_slots: List<AvailableSlot>,
    val experience: String,
    val name: String,
    val num_patient: String,
    val pic: String,
    val puid: Int,
    val rating: String,
    val speciality: String,
    val timings: Timings,
    val type: String,
    val vchat_url: String
)

data class AvailableSlot(
    val days: List<Day>,
    val month: String
)

data class Timings(
    val days: String,
    val time: String
)

data class Day(
    val date: Int,
    val day: String,
    val slots: List<String>
)