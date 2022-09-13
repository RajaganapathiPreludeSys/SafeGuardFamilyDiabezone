package com.safeguardFamily.diabezone.model.response

data class HomeResponse(
    var graph: Graph?,
    var is_member: Boolean?,
    var notifications: List<Notification>?
)

data class Graph(
    var last_30_days: GraphList?,
    var last_7_days: GraphList?,
    var lifetime: GraphList?
)

data class Notification(
    var expired_on: String?,
    var ndesc: String?,
    var nid: Int?,
    var pic: String?,
    var screen: String?,
    var title: String?
)

data class GraphList(
    var after_meal: GraphItems?,
    var before_meal: GraphItems?,
    var random: GraphItems?
)

data class GraphItems(
    var list: List<ListX>?,
    var summary: Summary?
)

data class ListX(
    var lid: String?,
    var log_value: Int?,
    var measure_date: String?,
    var measure_unit: String?,
    var period: String?,
    var uid: String?
)

data class Summary(
    var avg: Int?,
    var incident: Incident?,
    var max: String?,
    var min: String?,
    var target: Int?
)

data class Incident(
    var hyper: Int?,
    var hypo: Int?
)