package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.AccountDto
import com.marcosvperboni.bankingapp.backend.dto.StatementPageDto
import com.marcosvperboni.bankingapp.backend.module
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AccountRoutesTest {

    @Test
    fun `get account returns the caller's own account`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()

        val response = client.get("/accounts/${session.userId}") { bearer(session.token) }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(session.userId, response.body<AccountDto>().id)
    }

    @Test
    fun `get account without a token returns 401`() = testApplication {
        application { module() }
        val client = jsonClient()

        val response = client.get("/accounts/some-id")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `get account belonging to someone else returns 404`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()
        val other = client.registerTestAccount()

        val response = client.get("/accounts/${other.userId}") { bearer(session.token) }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `statement returns a page of transactions with pagination metadata`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()

        val response = client.get("/accounts/${session.userId}/statement?page=0&size=10") { bearer(session.token) }

        assertEquals(HttpStatusCode.OK, response.status)
        val page = response.body<StatementPageDto>()
        assertEquals(0, page.page)
        assertFalse(page.hasMore)
    }

    @Test
    fun `statement rejects an invalid page size with 400`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()

        val response = client.get("/accounts/${session.userId}/statement?page=0&size=0") { bearer(session.token) }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
