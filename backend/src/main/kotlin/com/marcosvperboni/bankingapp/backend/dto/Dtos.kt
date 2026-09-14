package com.marcosvperboni.bankingapp.backend.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(val message: String)

@Serializable
data class LoginRequestDto(val email: String = "", val password: String = "")

@Serializable
data class RegisterRequestDto(val email: String = "", val password: String = "", val fullName: String = "")

@Serializable
data class AuthResponseDto(val token: String, val userId: String, val email: String, val fullName: String)

@Serializable
data class AccountDto(val id: String, val ownerName: String, val balance: String, val currency: String)

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
data class StatementPageDto(val items: List<TransactionDto>, val page: Int, val size: Int, val hasMore: Boolean)

@Serializable
data class CreateTransferRequestDto(
    val fromAccountId: String = "",
    val toAccountId: String = "",
    val amount: String = "",
    val description: String = "",
)

@Serializable
data class TransferDto(
    val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: String,
    val description: String,
    val status: String,
    val createdAt: Long,
)

@Serializable
data class NotificationDto(val id: String, val title: String, val message: String, val read: Boolean, val createdAt: Long)
