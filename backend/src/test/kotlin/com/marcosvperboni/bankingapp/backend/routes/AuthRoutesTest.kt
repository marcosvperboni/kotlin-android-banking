package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.AuthResponseDto
import com.marcosvperboni.bankingapp.backend.dto.ErrorResponseDto
import com.marcosvperboni.bankingapp.backend.module
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthRoutesTest {

    @Test
    fun `register creates a new user and returns a token`() = testApplication {
        application { module() }
        val client = jsonClient()
        val email = "new-${System.nanoTime()}@test.com"

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"password123","fullName":"New User"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.body<AuthResponseDto>()
        assertEquals(email, body.email)
    }

    @Test
    fun `register rejects a short password with 400`() = testApplication {
        application { module() }
        val client = jsonClient()

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"short-${System.nanoTime()}@test.com","password":"123","fullName":"New User"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `register rejects a duplicate email with 400`() = testApplication {
        application { module() }
        val client = jsonClient()
        val email = "dup-${System.nanoTime()}@test.com"
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"password123","fullName":"First"}""")
        }

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"password123","fullName":"Second"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `login succeeds with the seeded demo user`() = testApplication {
        application { module() }
        val client = jsonClient()

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"demo@bank.com","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("demo@bank.com", response.body<AuthResponseDto>().email)
    }

    @Test
    fun `login rejects a wrong password with 401`() = testApplication {
        application { module() }
        val client = jsonClient()

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"demo@bank.com","password":"wrong-password"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("Invalid email or password", response.body<ErrorResponseDto>().message)
    }

    @Test
    fun `login rejects a blank email with 400`() = testApplication {
        application { module() }
        val client = jsonClient()

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"","password":"password123"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
