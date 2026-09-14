package com.marcosvperboni.bankingapp.domain.auth

data class User(
    val id: String,
    val email: String,
    val fullName: String,
)

data class AuthSession(
    val token: String,
    val user: User,
)
