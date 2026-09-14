package com.marcosvperboni.bankingapp.data.transfer.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTransferRequestDto(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: String,
    val description: String,
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
