package com.safeguardFamily.diabezone.model

import com.google.gson.annotations.SerializedName

class OnBoardingItem {
    @SerializedName("image")
    var image = 0
    @SerializedName("title")
    var title: String? = null
}