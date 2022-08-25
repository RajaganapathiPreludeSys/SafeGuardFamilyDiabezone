package com.safeguardFamily.diabezone.model.response

data class UserResponse(
    var is_new: Boolean,
    var user: User
)

data class User(
    var cc: String,
    var email: String,
    var mobile: String,
    var name: String,
    var pic: String,
    var uid: String
)