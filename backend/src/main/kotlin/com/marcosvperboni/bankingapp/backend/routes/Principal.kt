package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.errors.UnauthorizedException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

/** The authenticated account id, taken from the "userId" JWT claim (account id == user id). */
fun ApplicationCall.requireAccountId(): String =
    principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString()
        ?: throw UnauthorizedException("Missing or invalid token")
