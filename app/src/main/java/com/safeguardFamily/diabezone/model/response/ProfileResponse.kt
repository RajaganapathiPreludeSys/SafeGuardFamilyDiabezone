package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("health_coach") var health_coach: Provider?,
    @SerializedName("is_member") var is_member: Boolean?,
    @SerializedName("membership") var membership: List<Membership>?,
    @SerializedName("past_appointments") var past_appointments: List<PastAppointment>?,
    @SerializedName("user") var user: User?,
    @SerializedName("contact_info") var contactInfo: ContactInfo?
)

data class Membership(
    @SerializedName("mid") var mid: Int,
    @SerializedName("pack_id") var pack_id: Int,
    @SerializedName("pack_name") var pack_name: String,
    @SerializedName("uid") var uid: Long,
    @SerializedName("validity") var validity: String
)

data class PastAppointment(
    @SerializedName("aid") var aid: String?,
    @SerializedName("booking_date") var booking_date: String?,
    @SerializedName("booking_status") var booking_status: Int?,
    @SerializedName("provider") var provider: Provider?,
    @SerializedName("puid") var puid: String?,
    @SerializedName("slot") var slot: String?,
    @SerializedName("uid") var uid: String?
)

data class ContactInfo(
    @SerializedName("cc") var cc: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("pic") var pic: String?,
    @SerializedName("whatsapp_no") var whatsappNo: String?
)
