package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class HomeResponse(
    @SerializedName("graph") var graph: Graph?,
    @SerializedName("is_member") var is_member: Boolean?,
    @SerializedName("notifications") var notifications: List<Notification>?,
    @SerializedName("consolidate_pdf_url") var pdfUrl: String?
)

data class Graph(
    @SerializedName("last_30_days") var last_30_days: GraphList?,
    @SerializedName("last_7_days") var last_7_days: GraphList?,
    @SerializedName("lifetime") var lifetime: GraphList?
)

data class Notification(
    @SerializedName("ndate") var ndate: String?,
    @SerializedName("expired_on") var expired_on: String?,
    @SerializedName("ndesc") var ndesc: String?,
    @SerializedName("nid") var nid: Int?,
    @SerializedName("pic") var pic: String?,
    @SerializedName("screen") var screen: String?,
    @SerializedName("title") var title: String?
)

data class GraphList(
    @SerializedName("after_meal") var after_meal: GraphItems?,
    @SerializedName("before_meal") var before_meal: GraphItems?,
    @SerializedName("random") var random: GraphItems?
)

data class GraphItems(
    @SerializedName("list") var list: List<ListX>?,
    @SerializedName("summary") var summary: Summary?
)

data class ListX(
    @SerializedName("lid") var lid: String?,
    @SerializedName("log_value") var log_value: Int?,
    @SerializedName("measure_date") var measure_date: String?,
    @SerializedName("measure_unit") var measure_unit: String?,
    @SerializedName("period") var period: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("uid") var uid: String?

)

data class Summary(
    @SerializedName("avg") var avg: Int?,
    @SerializedName("incident") var incident: Incident?,
    @SerializedName("max") var max: String?,
    @SerializedName("min") var min: String?,
    @SerializedName("target") var target: Int?,
    @SerializedName("min_target") var minTarget: Int?,
    @SerializedName("max_target") var maxTarget: Int?
)

data class Incident(
    @SerializedName("hyper") var hyper: Int?,
    @SerializedName("hypo") var hypo: Int?
)