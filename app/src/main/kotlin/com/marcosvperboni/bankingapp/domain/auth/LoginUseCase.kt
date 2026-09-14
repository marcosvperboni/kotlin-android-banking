package com.marcosvperboni.bankingapp.domain.auth

import com.marcosvperboni.bankingapp.core.error.AppError
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthSession> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(AppError.Validation("email", "Enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure(AppError.Validation("password", "Password must be at least 6 characters"))
        }
        return authRepository.login(email.trim(), password)
    }
}
