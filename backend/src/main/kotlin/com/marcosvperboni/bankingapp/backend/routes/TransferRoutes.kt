package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.domain.TransferService
import com.marcosvperboni.bankingapp.backend.dto.CreateTransferRequestDto
import com.marcosvperboni.bankingapp.backend.dto.TransferDto
import com.marcosvperboni.bankingapp.backend.errors.NotFoundException
import com.marcosvperboni.bankingapp.backend.errors.ValidationException
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.transferRoutes() {
    authenticate("auth-jwt") {
        post("/transfers") {
            val callerId = call.requireAccountId()
            val request = call.receive<CreateTransferRequestDto>()
            if (request.fromAccountId != callerId) {
                throw ValidationException("fromAccountId must match the authenticated account")
            }
            val amount = request.amount.toBigDecimalOrNull()
                ?: throw ValidationException("Amount must be a valid decimal number")

            val transfer = TransferService.create(callerId, request.toAccountId, amount, request.description)
            call.respond(HttpStatusCode.Created, transfer.toDto())
        }

        get("/transfers/{id}") {
            val callerId = call.requireAccountId()
            val transferId = call.parameters["id"].orEmpty()
            val transfer = InMemoryStore.transfers[transferId] ?: throw NotFoundException("Transfer not found")
            if (transfer.fromAccountId != callerId && transfer.toAccountId != callerId) {
                throw NotFoundException("Transfer not found")
            }
            call.respond(transfer.toDto())
        }

        delete("/transfers/{id}") {
            val callerId = call.requireAccountId()
            val transferId = call.parameters["id"].orEmpty()
            val transfer = TransferService.cancel(transferId, callerId)
            call.respond(transfer.toDto())
        }

        get("/transfers") {
            val callerId = call.requireAccountId()
            val accountId = call.request.queryParameters["accountId"]
            if (accountId != null && accountId != callerId) {
                throw ValidationException("accountId must match the authenticated account")
            }
            val transfers = InMemoryStore.transfers.values
                .filter { it.fromAccountId == callerId || it.toAccountId == callerId }
                .sortedByDescending { it.createdAt }
            call.respond(transfers.map { it.toDto() })
        }
    }
}

private fun com.marcosvperboni.bankingapp.backend.domain.Transfer.toDto() = TransferDto(
    id = id,
    fromAccountId = fromAccountId,
    toAccountId = toAccountId,
    amount = amount.toPlainString(),
    description = description,
    status = status.name,
    createdAt = createdAt,
)
