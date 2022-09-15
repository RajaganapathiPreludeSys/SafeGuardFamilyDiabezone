package com.safeguardFamily.diabezone.model.response


data class ProvidersResponse(
    val appointments: List<Appointment>,
    val is_member: Boolean,
    val providers: List<Provider>
)

data class Appointment(
    val aid: String,
    val booking_date: String,
    val booking_status: Int,
    val provider: Provider,
    val puid: String,
    val slot: String,
    val uid: String
)

data class Provider(
    val about: String,
    var available_slots: List<AvailableSlot>?,
    val experience: String,
    val education: String,
    val languages: String,
    val reg_no: String,
    val fees: String,
    val name: String,
    val num_patient: String,
    val pic: String,
    val puid: String,
    val rating: String,
    val speciality: String,
    val timings: Timings,
    val type: String,
    val vchat_url: String,
    val mobile: String,
    val whatsapp_no: String,
    val cc: String
)

data class Timings(
    var days: Days?,
    var time: String?
)

data class Days(
    var fri: String?,
    var mon: String?,
    var sat: String?,
    var sun: String?,
    var thu: String?,
    var tue: String?,
    var wed: String?
)

data class AvailableSlot(
    val days: List<Day>,
    val month: String
)

data class Day(
    val date: Int,
    val day: String,
    val slots: List<String>
)