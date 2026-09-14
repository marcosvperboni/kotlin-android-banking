package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.TransferDto
import com.marcosvperboni.bankingapp.backend.module
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class TransferRoutesTest {

    @Test
    fun `create transfer succeeds and returns 201 with pending status`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")

        val response = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"25.00","description":"gift"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("PENDING", response.body<TransferDto>().status)
    }

    @Test
    fun `create transfer with insufficient funds returns 400`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("5.00")

        val response = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"25.00","description":"gift"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `create transfer to an unknown recipient returns 404`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")

        val response = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"missing-id","amount":"25.00","description":"gift"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `get transfer by id succeeds for a party to the transfer`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")
        val created = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"10.00","description":""}""")
        }.body<TransferDto>()

        val response = client.get("/transfers/${created.id}") { bearer(recipient.token) }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `get transfer by id returns 404 for an unrelated account`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        val outsider = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")
        val created = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"10.00","description":""}""")
        }.body<TransferDto>()

        val response = client.get("/transfers/${created.id}") { bearer(outsider.token) }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `cancel a pending transfer refunds the sender`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")
        val created = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"10.00","description":""}""")
        }.body<TransferDto>()

        val response = client.delete("/transfers/${created.id}") { bearer(sender.token) }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("CANCELLED", response.body<TransferDto>().status)
        assertEquals(BigDecimal("100.00"), InMemoryStore.accounts.getValue(sender.userId).balance)
    }

    @Test
    fun `cancel an already cancelled transfer returns 400`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")
        val created = client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"10.00","description":""}""")
        }.body<TransferDto>()
        client.delete("/transfers/${created.id}") { bearer(sender.token) }

        val response = client.delete("/transfers/${created.id}") { bearer(sender.token) }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `list transfers returns entries where the caller is sender or recipient`() = testApplication {
        application { module() }
        val client = jsonClient()
        val sender = client.registerTestAccount()
        val recipient = client.registerTestAccount()
        InMemoryStore.accounts.getValue(sender.userId).balance = BigDecimal("100.00")
        client.post("/transfers") {
            bearer(sender.token)
            contentType(ContentType.Application.Json)
            setBody("""{"fromAccountId":"${sender.userId}","toAccountId":"${recipient.userId}","amount":"10.00","description":""}""")
        }

        val response = client.get("/transfers?accountId=${sender.userId}") { bearer(sender.token) }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(1, response.body<List<TransferDto>>().size)
    }
}
