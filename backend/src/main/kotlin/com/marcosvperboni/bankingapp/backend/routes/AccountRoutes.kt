package com.marcosvperboni.bankingapp.backend.routes

import com.marcosvperboni.bankingapp.backend.dto.AccountDto
import com.marcosvperboni.bankingapp.backend.dto.StatementPageDto
import com.marcosvperboni.bankingapp.backend.dto.TransactionDto
import com.marcosvperboni.bankingapp.backend.errors.NotFoundException
import com.marcosvperboni.bankingapp.backend.errors.ValidationException
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.accountRoutes() {
    authenticate("auth-jwt") {
        get("/accounts/{id}") {
            val accountId = call.parameters["id"].orEmpty()
            val callerId = call.requireAccountId()
            if (accountId != callerId) throw NotFoundException("Account not found")

            val account = InMemoryStore.accounts[accountId] ?: throw NotFoundException("Account not found")
            call.respond(AccountDto(account.id, account.ownerName, account.balance.toPlainString(), account.currency))
        }

        get("/accounts/{id}/statement") {
            val accountId = call.parameters["id"].orEmpty()
            val callerId = call.requireAccountId()
            if (accountId != callerId) throw NotFoundException("Account not found")

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
            if (page < 0 || size <= 0) throw ValidationException("Invalid pagination parameters")

            val all = InMemoryStore.transactions[accountId].orEmpty().sortedByDescending { it.timestamp }
            val fromIndex = (page * size).coerceAtMost(all.size)
            val toIndex = (fromIndex + size).coerceAtMost(all.size)
            val pageItems = all.subList(fromIndex, toIndex).map {
                TransactionDto(it.id, it.accountId, it.description, it.amount.toPlainString(), it.direction.name, it.timestamp)
            }
            call.respond(StatementPageDto(pageItems, page, size, hasMore = toIndex < all.size))
        }
    }
}
