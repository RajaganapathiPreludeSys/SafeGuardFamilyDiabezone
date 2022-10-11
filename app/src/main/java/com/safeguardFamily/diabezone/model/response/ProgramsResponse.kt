package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class ProgramsResponse(
    var info: List<Info>?,
    var packages: List<Package>?
)

data class Info(
    var body: List<Body>?,
    var heading: String?,
    var expand: Boolean?,
)

data class Package(
    var content: List<Content>?,
    var discount: String?,
    @SerializedName("for_renew")
    var forRenew: Boolean?,
    var gst: String?,
    @SerializedName("is_subscribed")
    var isSubscribed: Boolean?,
    var message: String?,
    var mrp: String?,
    var pid: String?,
    var pname: String?,
    var price: String?,
    @SerializedName("program_fee")
    var programFee: String?,
    @SerializedName("show_button")
    var showButton: Boolean?
)

data class Body(
    var body: String?,
    var list: List<String>?,
    @SerializedName("sub-heading")
    var subHeading: String?,
    var type: String?
)

data class Content(
    var body: String?,
    var list: List<String>?,
    @SerializedName("sub-heading")
    var subHeading: String?,
    var type: String?
)