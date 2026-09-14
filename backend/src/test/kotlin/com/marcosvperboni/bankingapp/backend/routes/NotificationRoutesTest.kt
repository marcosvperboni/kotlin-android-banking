package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.NotificationDto
import com.marcosvperboni.bankingapp.backend.module
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRoutesTest {

    @Test
    fun `get notifications includes the welcome notification for a new account`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()

        val response = client.get("/notifications") { bearer(session.token) }

        assertEquals(HttpStatusCode.OK, response.status)
        val notifications = response.body<List<NotificationDto>>()
        assertEquals(1, notifications.size)
        assertTrue(notifications.none { it.read })
    }

    @Test
    fun `get notifications without a token returns 401`() = testApplication {
        application { module() }
        val client = jsonClient()

        val response = client.get("/notifications")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `mark as read updates the notification`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()
        val notification = client.get("/notifications") { bearer(session.token) }
            .body<List<NotificationDto>>()
            .first()

        val response = client.put("/notifications/${notification.id}") { bearer(session.token) }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.body<NotificationDto>().read)
    }

    @Test
    fun `mark unknown notification as read returns 404`() = testApplication {
        application { module() }
        val client = jsonClient()
        val session = client.registerTestAccount()

        val response = client.put("/notifications/does-not-exist") { bearer(session.token) }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
