package com.marcosvperboni.bankingapp.data.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class AuthResponseDto(
    val token: String,
    val userId: String,
    val email: String,
    val fullName: String,
)

@Serializable
data class ErrorResponseDto(val message: String)
