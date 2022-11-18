package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("is_new") var is_new: Boolean,
    @SerializedName("user") var user: User
)

data class User(
    @SerializedName("cc") var cc: String,
    @SerializedName("email") var email: String,
    @SerializedName("mobile") var mobile: String,
    @SerializedName("name") var name: String,
    @SerializedName("pic") var pic: String,
    @SerializedName("uname") var uname: String,
    @SerializedName("uid") var uid: String
)