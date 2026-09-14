package com.marcosvperboni.bankingapp.backend.domain

import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

enum class TransactionDirection { CREDIT, DEBIT }
enum class TransferStatus { PENDING, COMPLETED, CANCELLED }

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val passwordSalt: String,
)

/** Each demo user owns exactly one account, and the account id equals the user id. */
data class Account(
    val id: String,
    val ownerName: String,
    var balance: BigDecimal,
    val currency: String = "USD",
)

data class Transaction(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: BigDecimal,
    val direction: TransactionDirection,
    val timestamp: Long,
)

data class Transfer(
    val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: BigDecimal,
    val description: String,
    var status: TransferStatus,
    val createdAt: Long,
)

data class Notification(
    val id: String,
    val accountId: String,
    val title: String,
    val message: String,
    var read: Boolean,
    val createdAt: Long,
)

object IdGenerator {
    private val counter = AtomicLong(1000)
    fun next(prefix: String): String = "$prefix-${counter.incrementAndGet()}"
}
