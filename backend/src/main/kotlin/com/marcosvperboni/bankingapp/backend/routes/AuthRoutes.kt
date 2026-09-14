package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.config.JwtConfig
import com.marcosvperboni.bankingapp.backend.dto.AuthResponseDto
import com.marcosvperboni.bankingapp.backend.dto.LoginRequestDto
import com.marcosvperboni.bankingapp.backend.dto.RegisterRequestDto
import com.marcosvperboni.bankingapp.backend.errors.UnauthorizedException
import com.marcosvperboni.bankingapp.backend.errors.ValidationException
import com.marcosvperboni.bankingapp.backend.security.PasswordHasher
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes() {
    post("/auth/login") {
        val request = call.receive<LoginRequestDto>()
        if (request.email.isBlank() || request.password.isBlank()) {
            throw ValidationException("Email and password are required")
        }

        val user = InMemoryStore.usersByEmail[request.email]
            ?: throw UnauthorizedException("Invalid email or password")

        if (!PasswordHasher.matches(request.password, user.passwordSalt, user.passwordHash)) {
            throw UnauthorizedException("Invalid email or password")
        }

        val token = JwtConfig.generateToken(user.id)
        call.respond(HttpStatusCode.OK, AuthResponseDto(token, user.id, user.email, user.fullName))
    }

    post("/auth/register") {
        val request = call.receive<RegisterRequestDto>()
        if (!request.email.contains("@") || request.email.isBlank()) {
            throw ValidationException("A valid email is required")
        }
        if (request.password.length < 6) {
            throw ValidationException("Password must be at least 6 characters")
        }
        if (request.fullName.isBlank()) {
            throw ValidationException("Full name is required")
        }
        if (InMemoryStore.usersByEmail.containsKey(request.email)) {
            throw ValidationException("Email is already registered")
        }

        val user = InMemoryStore.registerUser(request.email, request.password, request.fullName)
        val token = JwtConfig.generateToken(user.id)
        call.respond(HttpStatusCode.Created, AuthResponseDto(token, user.id, user.email, user.fullName))
    }
}
