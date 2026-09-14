package com.marcosvperboni.bankingapp.data.auth

import com.marcosvperboni.bankingapp.data.auth.dto.LoginRequestDto
import com.marcosvperboni.bankingapp.data.common.safeApiCall
import com.marcosvperboni.bankingapp.domain.auth.AuthRepository
import com.marcosvperboni.bankingapp.domain.auth.AuthSession
import com.marcosvperboni.bankingapp.domain.auth.User
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenStorage: TokenStorage,
    private val json: Json,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthSession> =
        safeApiCall(json) { api.login(LoginRequestDto(email, password)) }.map { dto ->
            tokenStorage.saveSession(dto.token, dto.userId)
            AuthSession(
                token = dto.token,
                user = User(id = dto.userId, email = dto.email, fullName = dto.fullName),
            )
        }

    override suspend fun logout() {
        tokenStorage.clear()
    }

    override suspend fun currentUserId(): String? = tokenStorage.getUserId()

    override suspend fun isLoggedIn(): Boolean = tokenStorage.getToken() != null
}
