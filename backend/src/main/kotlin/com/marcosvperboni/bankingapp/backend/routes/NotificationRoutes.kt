package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.NotificationDto
import com.marcosvperboni.bankingapp.backend.errors.NotFoundException
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put

fun Route.notificationRoutes() {
    authenticate("auth-jwt") {
        get("/notifications") {
            val callerId = call.requireAccountId()
            val notifications = InMemoryStore.notifications[callerId].orEmpty().sortedByDescending { it.createdAt }
            call.respond(notifications.map { it.toDto() })
        }

        put("/notifications/{id}") {
            val callerId = call.requireAccountId()
            val notificationId = call.parameters["id"].orEmpty()
            val notification = InMemoryStore.notifications[callerId].orEmpty()
                .find { it.id == notificationId }
                ?: throw NotFoundException("Notification not found")
            notification.read = true
            call.respond(notification.toDto())
        }
    }
}

private fun com.marcosvperboni.bankingapp.backend.domain.Notification.toDto() =
    NotificationDto(id, title, message, read, createdAt)
