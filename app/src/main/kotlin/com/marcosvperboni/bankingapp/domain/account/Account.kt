package com.marcosvperboni.bankingapp.domain.account

import java.math.BigDecimal

data class Account(
    val id: String,
    val ownerName: String,
    val balance: BigDecimal,
    val currency: String,
)

enum class TransactionDirection { CREDIT, DEBIT }

data class AccountTransaction(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: BigDecimal,
    val direction: TransactionDirection,
    val timestampEpochMillis: Long,
)

data class PagedResult<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val hasMore: Boolean,
)
