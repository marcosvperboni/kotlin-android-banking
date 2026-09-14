package com.marcosvperboni.bankingapp.data.account.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: String,
    val ownerName: String,
    val balance: String,
    val currency: String,
)

@Serializable
data class TransactionDto(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: String,
    val direction: String,
    val timestamp: Long,
)

@Serializable
data class StatementPageDto(
    val items: List<TransactionDto>,
    val page: Int,
    val size: Int,
    val hasMore: Boolean,
)
