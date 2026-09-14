package com.marcosvperboni.bankingapp.backend.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date
import java.util.concurrent.TimeUnit

object JwtConfig {
    // ponytail: fixed demo secret with an env override, real deployment must set BANKING_JWT_SECRET.
    private val secret = System.getenv("BANKING_JWT_SECRET") ?: "demo-portfolio-secret-change-me"
    const val ISSUER = "kotlin-android-banking"
    const val AUDIENCE = "kotlin-android-banking-clients"
    const val REALM = "kotlin-android-banking"
    private val validityMs = TimeUnit.HOURS.toMillis(12)

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun generateToken(userId: String): String = JWT.create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + validityMs))
        .sign(algorithm)
}
