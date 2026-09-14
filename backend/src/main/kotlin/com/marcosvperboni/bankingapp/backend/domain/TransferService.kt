package com.marcosvperboni.bankingapp.backend.domain

import com.marcosvperboni.bankingapp.backend.errors.NotFoundException
import com.marcosvperboni.bankingapp.backend.errors.ValidationException
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import java.math.BigDecimal

/**
 * Holds all balance-mutating logic behind a single lock so concurrent transfer requests never
 * race on the same in-memory account.
 * ponytail: one global lock for all accounts, swap for per-account locks if throughput matters.
 */
object TransferService {
    private val lock = Any()

    fun create(fromAccountId: String, toAccountId: String, amount: BigDecimal, description: String): Transfer {
        if (toAccountId.isBlank()) throw ValidationException("toAccountId is required")
        if (toAccountId == fromAccountId) throw ValidationException("Cannot transfer to your own account")
        if (amount <= BigDecimal.ZERO) throw ValidationException("Amount must be greater than zero")

        synchronized(lock) {
            val fromAccount = InMemoryStore.accounts[fromAccountId] ?: throw NotFoundException("Account not found")
            InMemoryStore.accounts[toAccountId] ?: throw NotFoundException("Recipient account not found")
            if (fromAccount.balance < amount) throw ValidationException("Insufficient funds")

            fromAccount.balance -= amount
            val transfer = Transfer(
                id = IdGenerator.next("trf"),
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                amount = amount,
                description = description,
                status = TransferStatus.PENDING,
                createdAt = System.currentTimeMillis(),
            )
            InMemoryStore.transfers[transfer.id] = transfer
            return transfer
        }
    }

    fun cancel(transferId: String, requesterAccountId: String): Transfer {
        synchronized(lock) {
            val transfer = InMemoryStore.transfers[transferId] ?: throw NotFoundException("Transfer not found")
            if (transfer.fromAccountId != requesterAccountId) throw NotFoundException("Transfer not found")
            if (transfer.status != TransferStatus.PENDING) {
                throw ValidationException("Only pending transfers can be cancelled")
            }

            val fromAccount = InMemoryStore.accounts.getValue(transfer.fromAccountId)
            fromAccount.balance += transfer.amount
            transfer.status = TransferStatus.CANCELLED
            return transfer
        }
    }
}
