package com.marcosvperboni.bankingapp.data.auth

import com.marcosvperboni.bankingapp.data.auth.dto.AuthResponseDto
import com.marcosvperboni.bankingapp.data.auth.dto.LoginRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>
}
