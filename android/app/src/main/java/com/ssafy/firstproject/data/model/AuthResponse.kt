package com.ssafy.firstproject.data.model

data class AuthResponse(
    val success: Boolean,
    val accessToken: String,
    val error: String
)
