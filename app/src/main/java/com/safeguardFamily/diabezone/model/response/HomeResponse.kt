package com.safeguardFamily.diabezone.model.response

data class HomeResponse(
    var graph: Graph?,
    var is_member: Boolean?,
    var notifications: List<Notification>?
)

data class Graph(
    var last_30_days: Last30Days?,
    var last_7_days: Last7Days?,
    var lifetime: Lifetime?
)

data class Notification(
    var expired_on: String?,
    var ndesc: String?,
    var nid: Int?,
    var pic: String?,
    var screen: String?,
    var title: String?
)

data class Last30Days(
    var after_meal: AfterMeal?,
    var before_meal: BeforeMeal?,
    var random: Random?
)

data class Last7Days(
    var after_meal: AfterMeal?,
    var before_meal: BeforeMeal?,
    var random: Random?
)

data class Lifetime(
    var after_meal: AfterMeal?,
    var before_meal: BeforeMeal?,
    var random: Random?
)

data class AfterMeal(
    var list: List<ListX>?,
    var summary: Summary?
)

data class BeforeMeal(
    var list: List<ListX>?,
    var summary: Summary?
)

data class Random(
    var list: List<ListX>?,
    var summary: Summary?
)

data class ListX(
    var lid: String?,
    var log_value: String?,
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