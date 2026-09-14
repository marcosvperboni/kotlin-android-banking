package com.marcosvperboni.bankingapp.backend.domain

import com.marcosvperboni.bankingapp.backend.errors.NotFoundException
import com.marcosvperboni.bankingapp.backend.errors.ValidationException
import com.marcosvperboni.bankingapp.backend.store.InMemoryStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal

class TransferServiceTest {

    private fun newAccount(balance: String): String {
        val email = "user-${System.nanoTime()}@test.com"
        val user = InMemoryStore.registerUser(email, "password123", "Test User")
        InMemoryStore.accounts.getValue(user.id).balance = BigDecimal(balance)
        return user.id
    }

    @Test
    fun `create transfer deducts amount from sender balance and stays pending`() {
        val sender = newAccount("100.00")
        val recipient = newAccount("0.00")

        val transfer = TransferService.create(sender, recipient, BigDecimal("40.00"), "rent")

        assertEquals(TransferStatus.PENDING, transfer.status)
        assertEquals(BigDecimal("60.00"), InMemoryStore.accounts.getValue(sender).balance)
        assertEquals(BigDecimal("0.00"), InMemoryStore.accounts.getValue(recipient).balance)
    }

    @Test
    fun `create transfer rejects amount above available balance`() {
        val sender = newAccount("10.00")
        val recipient = newAccount("0.00")

        assertThrows(ValidationException::class.java) {
            TransferService.create(sender, recipient, BigDecimal("50.00"), "too much")
        }
        assertEquals(BigDecimal("10.00"), InMemoryStore.accounts.getValue(sender).balance)
    }

    @Test
    fun `create transfer rejects zero or negative amount`() {
        val sender = newAccount("100.00")
        val recipient = newAccount("0.00")

        assertThrows(ValidationException::class.java) {
            TransferService.create(sender, recipient, BigDecimal.ZERO, "invalid")
        }
    }

    @Test
    fun `create transfer rejects sending to the same account`() {
        val sender = newAccount("100.00")

        assertThrows(ValidationException::class.java) {
            TransferService.create(sender, sender, BigDecimal("10.00"), "self")
        }
    }

    @Test
    fun `create transfer rejects unknown recipient`() {
        val sender = newAccount("100.00")

        assertThrows(NotFoundException::class.java) {
            TransferService.create(sender, "does-not-exist", BigDecimal("10.00"), "ghost")
        }
    }

    @Test
    fun `cancel refunds the sender and marks transfer cancelled`() {
        val sender = newAccount("100.00")
        val recipient = newAccount("0.00")
        val transfer = TransferService.create(sender, recipient, BigDecimal("30.00"), "trip")

        val cancelled = TransferService.cancel(transfer.id, sender)

        assertEquals(TransferStatus.CANCELLED, cancelled.status)
        assertEquals(BigDecimal("100.00"), InMemoryStore.accounts.getValue(sender).balance)
    }

    @Test
    fun `cancel rejects a transfer that is not pending`() {
        val sender = newAccount("100.00")
        val recipient = newAccount("0.00")
        val transfer = TransferService.create(sender, recipient, BigDecimal("30.00"), "trip")
        TransferService.cancel(transfer.id, sender)

        assertThrows(ValidationException::class.java) {
            TransferService.cancel(transfer.id, sender)
        }
    }

    @Test
    fun `cancel rejects a requester who is not the sender`() {
        val sender = newAccount("100.00")
        val recipient = newAccount("0.00")
        val transfer = TransferService.create(sender, recipient, BigDecimal("30.00"), "trip")

        assertThrows(NotFoundException::class.java) {
            TransferService.cancel(transfer.id, recipient)
        }
    }
}
