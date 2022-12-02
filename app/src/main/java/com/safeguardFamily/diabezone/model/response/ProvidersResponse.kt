package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class ProvidersResponse(
    @SerializedName("appointments") var appointments: List<Appointment>,
    @SerializedName("is_member") var is_member: Boolean,
    @SerializedName("providers") var providers: List<Provider>
)

data class Appointment(
    @SerializedName("aid") var aid: String,
    @SerializedName("booking_date") var booking_date: String,
    @SerializedName("booking_status") var booking_status: Int,
    @SerializedName("provider") var provider: Provider,
    @SerializedName("puid") var puid: String,
    @SerializedName("slot") var slot: String,
    @SerializedName("uid") var uid: String
)

data class Provider(
    @SerializedName("about") var about: String,
    @SerializedName("available_slots") var available_slots: List<AvailableSlot>?,
    @SerializedName("experience") var experience: String,
    @SerializedName("education") var education: String,
    @SerializedName("languages") var languages: String,
    @SerializedName("reg_no") var reg_no: String,
    @SerializedName("fees") var fees: String,
    @SerializedName("name") var name: String,
    @SerializedName("num_patient") var num_patient: String,
    @SerializedName("num_consultations") var num_consultations: String,
    @SerializedName("pic") var pic: String,
    @SerializedName("puid") var puid: String,
    @SerializedName("rating") var rating: String,
    @SerializedName("speciality") var speciality: String,
    @SerializedName("timings") var timings: Timings,
    @SerializedName("type") var type: String,
    @SerializedName("vchat_url") var vchat_url: String,
    @SerializedName("mobile") var mobile: String,
    @SerializedName("whatsapp") var whatsapp: String,
    @SerializedName("category") var category: String,
    @SerializedName("is_free") var isFree: Boolean,
    @SerializedName("cc") var cc: String
)

data class Timings(
    @SerializedName("days") var days: Days?,
    @SerializedName("time") var time: String?
)

data class Days(
    @SerializedName("fri") var fri: String?,
    @SerializedName("mon") var mon: String?,
    @SerializedName("sat") var sat: String?,
    @SerializedName("sun") var sun: String?,
    @SerializedName("thu") var thu: String?,
    @SerializedName("tue") var tue: String?,
    @SerializedName("wed") var wed: String?
)

data class AvailableSlot(
    @SerializedName("days") var days: List<Day>,
    @SerializedName("month") var month: String
)

data class Day(
    @SerializedName("date") var date: Int,
    @SerializedName("day") var day: String,
    @SerializedName("slots") var slots: List<String>
)