package com.example.qualitywash.network.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class ResetPasswordRequest(
    val email: String,
    val run: String,
    val newPassword: String
)
