package com.marcosvperboni.bankingapp.domain.transfer

import java.math.BigDecimal

enum class TransferStatus { PENDING, COMPLETED, CANCELLED }

data class Transfer(
    val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: BigDecimal,
    val description: String,
    val status: TransferStatus,
    val createdAtEpochMillis: Long,
)
