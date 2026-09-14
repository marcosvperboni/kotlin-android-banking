package com.marcosvperboni.bankingapp.data.transfer

import com.marcosvperboni.bankingapp.data.transfer.dto.TransferDto
import com.marcosvperboni.bankingapp.domain.transfer.Transfer
import com.marcosvperboni.bankingapp.domain.transfer.TransferStatus
import java.math.BigDecimal

fun TransferDto.toDomain() = Transfer(
    id = id,
    fromAccountId = fromAccountId,
    toAccountId = toAccountId,
    amount = BigDecimal(amount),
    description = description,
    status = TransferStatus.valueOf(status),
    createdAtEpochMillis = createdAt,
)
