package com.marcosvperboni.bankingapp.domain.auth

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthSession>
    suspend fun logout()
    suspend fun currentUserId(): String?
    suspend fun isLoggedIn(): Boolean
}
