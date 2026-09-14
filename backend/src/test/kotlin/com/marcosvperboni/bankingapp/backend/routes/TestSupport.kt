package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.AuthResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder

fun ApplicationTestBuilder.jsonClient(): HttpClient = createClient {
    install(ContentNegotiation) { json() }
}

/** Registers a brand-new demo account and returns its bearer token + account id. */
suspend fun HttpClient.registerTestAccount(fullName: String = "Test User"): AuthResponseDto {
    val email = "test-${System.nanoTime()}@test.com"
    val response = post("/auth/register") {
        contentType(ContentType.Application.Json)
        setBody("""{"email":"$email","password":"password123","fullName":"$fullName"}""")
    }
    return response.body()
}

fun io.ktor.client.request.HttpRequestBuilder.bearer(token: String) {
    header("Authorization", "Bearer $token")
}
