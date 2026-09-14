package com.marcosvperboni.bankingapp.domain.transfer

import java.math.BigDecimal

interface TransferRepository {
    suspend fun createTransfer(fromAccountId: String, toAccountId: String, amount: BigDecimal, description: String): Result<Transfer>
    suspend fun getTransfer(transferId: String): Result<Transfer>
    suspend fun cancelTransfer(transferId: String): Result<Unit>
    suspend fun listTransfers(accountId: String): Result<List<Transfer>>
}
