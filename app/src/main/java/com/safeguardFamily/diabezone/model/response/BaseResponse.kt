package com.safeguardFamily.diabezone.model.response

data class BaseResponse<T>(
    val success: Boolean?,
    val error: String?,
    val data: T
)