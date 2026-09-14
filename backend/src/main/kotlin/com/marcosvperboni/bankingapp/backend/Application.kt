package com.marcosvperboni.bankingapp.backend

import com.auth0.jwt.exceptions.JWTVerificationException
import com.marcosvperboni.bankingapp.backend.config.JwtConfig
import com.marcosvperboni.bankingapp.backend.dto.ErrorResponseDto
import com.marcosvperboni.bankingapp.backend.errors.NotFoundException
import com.marcosvperboni.bankingapp.backend.errors.UnauthorizedException
import com.marcosvperboni.bankingapp.backend.errors.ValidationException
import com.marcosvperboni.bankingapp.backend.routes.accountRoutes
import com.marcosvperboni.bankingapp.backend.routes.authRoutes
import com.marcosvperboni.bankingapp.backend.routes.notificationRoutes
import com.marcosvperboni.bankingapp.backend.routes.transferRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.ContentTransformationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    install(CallLogging)

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.REALM
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) JWTPrincipal(credential.payload) else null
            }
        }
    }

    install(StatusPages) {
        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponseDto(cause.message ?: "Invalid request"))
        }
        exception<UnauthorizedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponseDto(cause.message ?: "Unauthorized"))
        }
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponseDto(cause.message ?: "Not found"))
        }
        exception<ContentTransformationException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Malformed request body"))
        }
        exception<JWTVerificationException> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, ErrorResponseDto("Invalid or expired token"))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponseDto(cause.message ?: "Internal server error"))
        }
    }

    routing {
        authRoutes()
        accountRoutes()
        transferRoutes()
        notificationRoutes()
    }
}
