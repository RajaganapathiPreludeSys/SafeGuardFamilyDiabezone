package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class ProgramsResponse(
    @SerializedName("info") var info: List<Info>?,
    @SerializedName("packages") var packages: List<Package>?
)

data class Info(
    @SerializedName("body") var body: List<Body>?,
    @SerializedName("heading") var heading: String?,
    @SerializedName("expand") var expand: Boolean?,
)

data class Package(
    @SerializedName("content") var content: List<Content>?,
    @SerializedName("discount") var discount: String?,
    @SerializedName("for_renew") var forRenew: Boolean?,
    @SerializedName("gst") var gst: String?,
    @SerializedName("is_subscribed") var isSubscribed: Boolean?,
    @SerializedName("message") var message: String?,
    @SerializedName("mrp") var mrp: String?,
    @SerializedName("pid") var pid: String?,
    @SerializedName("pname") var pname: String?,
    @SerializedName("price") var price: String?,
    @SerializedName("program_fee") var programFee: String?,
    @SerializedName("show_button") var showButton: Boolean?
)

data class Body(
    @SerializedName("body") var body: String?,
    @SerializedName("list") var list: List<String>?,
    @SerializedName("sub-heading") var subHeading: String?,
    @SerializedName("type") var type: String?
)

data class Content(
    @SerializedName("body") var body: String?,
    @SerializedName("list") var list: List<String>?,
    @SerializedName("sub-heading") var subHeading: String?,
    @SerializedName("type") var type: String?
)