package com.gamerboard.live.models

data class ApiResponse<T>(
    val error: String? = null,
    val response: T? = null
)