package com.marcosvperboni.bankingapp.data.account

import com.marcosvperboni.bankingapp.data.account.dto.AccountDto
import com.marcosvperboni.bankingapp.data.account.dto.TransactionDto
import com.marcosvperboni.bankingapp.data.account.local.AccountEntity
import com.marcosvperboni.bankingapp.data.account.local.TransactionEntity
import com.marcosvperboni.bankingapp.domain.account.Account
import com.marcosvperboni.bankingapp.domain.account.AccountTransaction
import com.marcosvperboni.bankingapp.domain.account.TransactionDirection
import java.math.BigDecimal

fun AccountDto.toDomain() = Account(id, ownerName, BigDecimal(balance), currency)

fun AccountDto.toEntity() = AccountEntity(id, ownerName, balance, currency)

fun AccountEntity.toDomain() = Account(id, ownerName, BigDecimal(balance), currency)

fun TransactionDto.toDomain() = AccountTransaction(
    id = id,
    accountId = accountId,
    description = description,
    amount = BigDecimal(amount),
    direction = TransactionDirection.valueOf(direction),
    timestampEpochMillis = timestamp,
)

fun TransactionDto.toEntity() = TransactionEntity(id, accountId, description, amount, direction, timestamp)

fun TransactionEntity.toDomain() = AccountTransaction(
    id = id,
    accountId = accountId,
    description = description,
    amount = BigDecimal(amount),
    direction = TransactionDirection.valueOf(direction),
    timestampEpochMillis = timestamp,
)
