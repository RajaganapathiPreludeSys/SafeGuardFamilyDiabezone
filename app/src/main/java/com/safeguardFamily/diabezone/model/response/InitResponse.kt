package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class InitResponse(
    @SerializedName("force_update") var forceUpdate: Boolean?,
    @SerializedName("latest_version") var latestVersion: String?
)